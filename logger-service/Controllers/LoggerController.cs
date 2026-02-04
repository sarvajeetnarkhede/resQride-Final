using Microsoft.AspNetCore.Mvc;
using logger_service.Models;
using logger_service.Services;

namespace logger_service.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class LoggerController : ControllerBase
    {
        private readonly IFileLoggerService _loggerService;
        private readonly ILogger<LoggerController> _logger;

        public LoggerController(IFileLoggerService loggerService, ILogger<LoggerController> logger)
        {
            _loggerService = loggerService;
            _logger = logger;
        }

        [HttpPost("log")]
        public async Task<IActionResult> LogEndpoint([FromBody] LogEntry logEntry)
        {
            try
            {
                if (logEntry == null)
                {
                    return BadRequest("Log entry is required");
                }

                // Set timestamp if not provided
                if (logEntry.Timestamp == default)
                {
                    logEntry.Timestamp = DateTime.UtcNow;
                }

                await _loggerService.LogAsync(logEntry);
                return Ok(new { message = "Log entry recorded successfully" });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error logging endpoint hit");
                return StatusCode(500, "Internal server error while logging");
            }
        }

        [HttpGet("logs")]
        public async Task<IActionResult> GetLogs([FromQuery] string? date = null, [FromQuery] string? service = null)
        {
            try
            {
                var logs = await _loggerService.GetLogsAsync(date, service);
                return Ok(logs);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error retrieving logs");
                return StatusCode(500, "Internal server error while retrieving logs");
            }
        }

        [HttpGet("health")]
        public async Task<IActionResult> HealthCheck()
        {
            try
            {
                var isHealthy = await _loggerService.IsHealthyAsync();
                return Ok(new { 
                    status = isHealthy ? "healthy" : "unhealthy",
                    timestamp = DateTime.UtcNow,
                    service = "logger-service"
                });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Health check failed");
                return StatusCode(500, new { 
                    status = "unhealthy", 
                    timestamp = DateTime.UtcNow,
                    service = "logger-service",
                    error = ex.Message
                });
            }
        }

        [HttpGet("services")]
        public IActionResult GetAvailableServices()
        {
            try
            {
                var logDirectory = Path.Combine(Directory.GetCurrentDirectory(), "logs");
                if (!Directory.Exists(logDirectory))
                {
                    return Ok(new string[0]);
                }

                var files = Directory.GetFiles(logDirectory, "*.log");
                var services = new HashSet<string>();

                foreach (var file in files)
                {
                    var lines = System.IO.File.ReadLines(file).Take(100); // Sample first 100 lines
                    foreach (var line in lines)
                    {
                        var parts = line.Split(new[] { " - " }, StringSplitOptions.None);
                        if (parts.Length >= 3)
                        {
                            services.Add(parts[2].Trim());
                        }
                    }
                }

                return Ok(services.ToList());
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error retrieving services");
                return StatusCode(500, "Internal server error while retrieving services");
            }
        }
    }
}
