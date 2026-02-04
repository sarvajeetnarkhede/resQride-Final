using logger_service.Models;

namespace logger_service.Services
{
    public interface IFileLoggerService
    {
        Task LogAsync(LogEntry logEntry);
        Task<IEnumerable<LogEntry>> GetLogsAsync(string? date = null, string? serviceName = null);
        Task<bool> IsHealthyAsync();
    }
}
