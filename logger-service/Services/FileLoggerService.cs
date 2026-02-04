using logger_service.Models;

namespace logger_service.Services
{
    public class FileLoggerService : IFileLoggerService
    {
        private readonly string _logDirectory;
        private readonly object _lock = new object();
        private readonly ILogger<FileLoggerService> _logger;

        public FileLoggerService(IConfiguration configuration, ILogger<FileLoggerService> logger)
        {
            _logger = logger;
            _logDirectory = configuration["LoggerSettings:LogDirectory"] ?? "logs";
            
            if (!Directory.Exists(_logDirectory))
            {
                Directory.CreateDirectory(_logDirectory);
            }
        }

        public Task LogAsync(LogEntry logEntry)
        {
            var fileName = $"{DateTime.UtcNow:yyyy-MM-dd}.log";
            var filePath = Path.Combine(_logDirectory, fileName);
            
            var logLine = $"[{logEntry.Timestamp:yyyy-MM-dd HH:mm:ss.fff}] {logEntry.Method} {logEntry.Endpoint} - {logEntry.StatusCode} - {logEntry.ServiceName} - {logEntry.IpAddress} - {logEntry.UserAgent} - {logEntry.ResponseTimeMs}ms";
            
            if (!string.IsNullOrEmpty(logEntry.UserId))
            {
                logLine += $" - User:{logEntry.UserId}";
            }
            
            if (!string.IsNullOrEmpty(logEntry.RequestId))
            {
                logLine += $" - Req:{logEntry.RequestId}";
            }
            
            logLine += Environment.NewLine;

            lock (_lock)
            {
                System.IO.File.AppendAllText(filePath, logLine);
            }

            _logger.LogInformation("Logged: {Method} {Endpoint} from {ServiceName}", 
                logEntry.Method, logEntry.Endpoint, logEntry.ServiceName);
            
            return Task.CompletedTask;
        }

        public async Task<IEnumerable<LogEntry>> GetLogsAsync(string? date = null, string? serviceName = null)
        {
            var targetDate = date ?? DateTime.UtcNow.ToString("yyyy-MM-dd");
            var fileName = $"{targetDate}.log";
            var filePath = Path.Combine(_logDirectory, fileName);

            if (!System.IO.File.Exists(filePath))
            {
                return Enumerable.Empty<LogEntry>();
            }

            var logs = new List<LogEntry>();
            var lines = await System.IO.File.ReadAllLinesAsync(filePath);

            foreach (var line in lines)
            {
                try
                {
                    var logEntry = ParseLogLine(line);
                    if (logEntry != null)
                    {
                        if (string.IsNullOrEmpty(serviceName) || logEntry.ServiceName.Equals(serviceName, StringComparison.OrdinalIgnoreCase))
                        {
                            logs.Add(logEntry);
                        }
                    }
                }
                catch
                {
                    // Skip malformed lines
                    continue;
                }
            }

            return logs.OrderByDescending(l => l.Timestamp);
        }

        public async Task<bool> IsHealthyAsync()
        {
            try
            {
                var testFile = Path.Combine(_logDirectory, "health-check.log");
                await System.IO.File.WriteAllTextAsync(testFile, $"Health check at {DateTime.UtcNow}");
                System.IO.File.Delete(testFile);
                return true;
            }
            catch
            {
                return false;
            }
        }

        private LogEntry? ParseLogLine(string line)
        {
            // Format: [2026-02-02 10:47:23.456] GET /api/users/profile - 200 - gateway - 192.168.1.100 - Mozilla/5.0... - 150ms
            try
            {
                var parts = line.Split(new string[] { "] " }, 2, StringSplitOptions.None);
                if (parts.Length < 2) return null;

                var timestampStr = parts[0].TrimStart('[');
                if (!DateTime.TryParseExact(timestampStr, "yyyy-MM-dd HH:mm:ss.fff", null, System.Globalization.DateTimeStyles.None, out var timestamp))
                {
                    return null;
                }

                var remaining = parts[1];
                var details = remaining.Split(new string[] { " - " }, StringSplitOptions.None);
                
                if (details.Length < 6) return null;

                var methodEndpoint = details[0].Split(' ', 2);
                if (methodEndpoint.Length < 2) return null;

                return new LogEntry
                {
                    Timestamp = timestamp,
                    Method = methodEndpoint[0],
                    Endpoint = methodEndpoint[1],
                    StatusCode = int.TryParse(details[1], out var code) ? code : 0,
                    ServiceName = details[2],
                    IpAddress = details[3],
                    UserAgent = details[4],
                    ResponseTimeMs = ParseResponseTime(details[5])
                };
            }
            catch
            {
                return null;
            }
        }

        private long ParseResponseTime(string responseTimeStr)
        {
            // Extract number from "150ms" or similar
            var match = System.Text.RegularExpressions.Regex.Match(responseTimeStr, @"(\d+)ms");
            if (match.Success && long.TryParse(match.Groups[1].Value, out var ms))
            {
                return ms;
            }
            return 0;
        }
    }
}
