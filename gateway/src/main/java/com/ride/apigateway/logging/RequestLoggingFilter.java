package com.ride.apigateway.logging;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Component
@Order(-50) // Runs after JWT auth but before response
public class RequestLoggingFilter implements GlobalFilter {

    private final WebClient webClient;

    public RequestLoggingFilter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024)) // 1MB
                .build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        String requestId = java.util.UUID.randomUUID().toString().substring(0, 8);
        
        // Add request ID to headers for tracing
        exchange.getRequest().mutate().header("X-Request-Id", requestId);
        
        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    // Skip logging for logger service endpoints
                    String path = exchange.getRequest().getURI().getPath();
                    if (path.startsWith("/api/logger/")) {
                        return;
                    }
                    
                    // Send log to logger service asynchronously (fire-and-forget)
                    logRequest(exchange, startTime, requestId);
                }));
    }

    private void logRequest(ServerWebExchange exchange, long startTime, String requestId) {
        try {
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;
            
            String method = exchange.getRequest().getMethod().name();
            String path = exchange.getRequest().getURI().getPath();
            String queryString = exchange.getRequest().getURI().getQuery();
            String endpoint = queryString != null ? path + "?" + queryString : path;
            
            int statusCode = exchange.getResponse().getStatusCode() != null ? 
                    exchange.getResponse().getStatusCode().value() : 0;
            
            String userAgent = exchange.getRequest().getHeaders().getFirst("User-Agent");
            String ipAddress = getClientIpAddress(exchange);
            String userEmail = exchange.getRequest().getHeaders().getFirst("X-User-Email");
            
            // Create log entry
            LogEntry logEntry = new LogEntry();
            logEntry.setTimestamp(Instant.now());
            logEntry.setMethod(method);
            logEntry.setEndpoint(endpoint);
            logEntry.setStatusCode(statusCode);
            logEntry.setServiceName("gateway");
            logEntry.setUserAgent(userAgent != null ? userAgent : "Unknown");
            logEntry.setIpAddress(ipAddress);
            logEntry.setResponseTimeMs(responseTime);
            logEntry.setRequestId(requestId);
            logEntry.setUserId(userEmail);

            // Send to logger service asynchronously
            webClient.post()
                    .uri("http://localhost:9090/api/logger/log")
                    .bodyValue(logEntry)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .timeout(Duration.ofMillis(500)) // Don't block for too long
                    .doOnError(error -> System.err.println("Failed to log request: " + error.getMessage()))
                    .subscribe();
                    
        } catch (Exception e) {
            System.err.println("Error in logging filter: " + e.getMessage());
            // Don't rethrow - logging failures shouldn't break the request
        }
    }

    private String getClientIpAddress(ServerWebExchange exchange) {
        String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = exchange.getRequest().getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return exchange.getRequest().getRemoteAddress() != null ? 
                exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : "Unknown";
    }

    public static class LogEntry {
        private Instant timestamp;
        private String method;
        private String endpoint;
        private int statusCode;
        private String serviceName;
        private String userAgent;
        private String ipAddress;
        private long responseTimeMs;
        private String requestId;
        private String userId;

        // Getters and Setters
        public Instant getTimestamp() { return timestamp; }
        public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
        
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        
        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
        
        public int getStatusCode() { return statusCode; }
        public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
        
        public String getServiceName() { return serviceName; }
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }
        
        public String getUserAgent() { return userAgent; }
        public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
        
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        
        public long getResponseTimeMs() { return responseTimeMs; }
        public void setResponseTimeMs(long responseTimeMs) { this.responseTimeMs = responseTimeMs; }
        
        public String getRequestId() { return requestId; }
        public void setRequestId(String requestId) { this.requestId = requestId; }
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }
}
