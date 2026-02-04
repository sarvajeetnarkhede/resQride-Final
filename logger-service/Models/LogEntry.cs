namespace logger_service.Models
{
    public class LogEntry
    {
        public DateTime Timestamp { get; set; }
        public string Method { get; set; } = string.Empty;
        public string Endpoint { get; set; } = string.Empty;
        public int StatusCode { get; set; }
        public string ServiceName { get; set; } = string.Empty;
        public string UserAgent { get; set; } = string.Empty;
        public string IpAddress { get; set; } = string.Empty;
        public long ResponseTimeMs { get; set; }
        public string? UserId { get; set; }
        public string? RequestId { get; set; }
    }
}
