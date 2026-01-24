package com.ride.apigateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(-100)
public class JwtAuthFilter implements GlobalFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    private boolean isPublic(String path, HttpMethod method) {
        if (path.startsWith("/api/auth")) return true;
        
        // Explicitly require authentication for /api/location/me
        if (path.equals("/api/location/me") || path.equals("/api/location/me/")) {
            return false;
        }

        // Only allow GET requests to the base list and categories to be public.
        boolean isPublicGet = method == HttpMethod.GET &&
                (path.equals("/api/requests")
                        || path.equals("/api/requests/")
                        || path.startsWith("/api/mechanics/")
                        || path.startsWith("/api/mechanics")
                        || path.startsWith("/api/feedback/")
                        || path.startsWith("/api/feedback")
                        || path.startsWith("/api/location/")
                        || path.equals("/api/location")
                        || path.startsWith("/api/payments/")
                        || path.equals("/api/payments")
                );
        
        return isPublicGet;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        if (isPublic(path, method)) {
            System.out.println("Gateway: Public route accessed -> " + method + " " + path);
            return chain.filter(exchange);
        }

        System.out.println("Gateway: Protected route accessed -> " + method + " " + path);

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        String email = jwtUtil.getEmail(token);
        String role = jwtUtil.getRole(token);

        System.out.println("Gateway injecting → " + email);

        ServerWebExchange mutated = exchange.mutate()
                .request(r -> r
                        .headers(headers -> {
                            headers.remove(HttpHeaders.AUTHORIZATION);
                        })
                        .header("X-User-Email", email)
                        .header("X-User-Role", role)
                )
                .build();

        return chain.filter(mutated);
    }
}
