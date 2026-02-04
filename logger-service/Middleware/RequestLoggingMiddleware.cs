namespace logger_service.Middleware
{
    public class RequestLoggingMiddleware
    {
        private readonly RequestDelegate _next;
        private readonly ILogger<RequestLoggingMiddleware> _logger;

        public RequestLoggingMiddleware(RequestDelegate next, ILogger<RequestLoggingMiddleware> logger)
        {
            _next = next;
            _logger = logger;
        }

        public async Task InvokeAsync(HttpContext context)
        {
            var startTime = DateTime.UtcNow;
            var requestId = Guid.NewGuid().ToString("N")[..8];
            
            // Add request ID to response headers for tracing
            context.Response.OnStarting(() =>
            {
                context.Response.Headers["X-Request-Id"] = requestId;
                return Task.CompletedTask;
            });

            try
            {
                await _next(context);
            }
            finally
            {
                var endTime = DateTime.UtcNow;
                var responseTime = (endTime - startTime).TotalMilliseconds;

                var logEntry = new Models.LogEntry
                {
                    Timestamp = startTime,
                    Method = context.Request.Method,
                    Endpoint = context.Request.Path + context.Request.QueryString,
                    StatusCode = context.Response.StatusCode,
                    ServiceName = "logger-service",
                    UserAgent = context.Request.Headers["User-Agent"].FirstOrDefault() ?? "Unknown",
                    IpAddress = GetClientIpAddress(context),
                    ResponseTimeMs = (long)responseTime,
                    RequestId = requestId
                };

                // Log to file service
                var fileLogger = context.RequestServices.GetRequiredService<Services.IFileLoggerService>();
                _ = Task.Run(async () => await fileLogger.LogAsync(logEntry));

                _logger.LogInformation("Request {Method} {Endpoint} - {StatusCode} - {ResponseTime}ms", 
                    logEntry.Method, logEntry.Endpoint, logEntry.StatusCode, logEntry.ResponseTimeMs);
            }
        }

        private string GetClientIpAddress(HttpContext context)
        {
            var ipAddress = context.Request.Headers["X-Forwarded-For"].FirstOrDefault();
            
            if (!string.IsNullOrEmpty(ipAddress))
            {
                var addresses = ipAddress.Split(',');
                if (addresses.Length != 0)
                {
                    return addresses[0].Trim();
                }
            }

            ipAddress = context.Request.Headers["X-Real-IP"].FirstOrDefault();
            
            if (!string.IsNullOrEmpty(ipAddress))
            {
                return ipAddress;
            }

            return context.Connection.RemoteIpAddress?.ToString() ?? "Unknown";
        }
    }
}
