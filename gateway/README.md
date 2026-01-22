# ShopVerse API Gateway

## Overview
The API Gateway is the single entry point for all client requests to the ShopVerse microservices. It handles routing, authentication, rate limiting, and other cross-cutting concerns.

## Features
- **Request Routing**: Routes requests to appropriate microservices
- **JWT Authentication**: Validates JWT tokens for protected endpoints
- **Service Discovery**: Integrates with Eureka for dynamic service discovery
- **Circuit Breaker**: Implements resilience patterns with Resilience4j
- **Rate Limiting**: Protects against abuse with request rate limiting
- **Request/Response Logging**: Logs incoming requests and outgoing responses

## Prerequisites
- Java 21+
- Spring Boot 3.5.7
- Spring Cloud 2025.0.0
- Eureka Server (discovery-service)
- Config Server (config-server)
- Redis (for rate limiting)

## Configuration

### Environment Variables
```bash
# Required for Config Server
GITHUB_USERNAME=your-github-username
GITHUB_TOKEN=your-github-token

# JWT Secret (should match user-service)
JWT_SECRET=your-256-bit-secret

# Redis Configuration (for rate limiting)
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379
```

### Routes
| Route | Service | Description |
|-------|---------|-------------|
| `/api/auth/**` | user-service | Authentication endpoints |
| `/api/users/**` | user-service | User management endpoints |
| `/eureka/**` | discovery-service | Eureka dashboard and API |
| `/actuator/**` | api-gateway | Actuator endpoints |

## Running the API Gateway

### Prerequisites
1. Start the Config Server
2. Start the Discovery Service (Eureka)
3. Start Redis (for rate limiting)
4. Ensure the config repository is accessible

### Running with Gradle
```bash
./gradlew bootRun
```

The API Gateway will be available at: http://localhost:8080

## Security
- All endpoints except `/api/auth/**` and `/actuator/health` require a valid JWT token
- Rate limiting is applied to prevent abuse
- Sensitive headers are stripped from downstream services

## Monitoring
- Eureka Dashboard: http://localhost:8761
- Actuator Endpoints: http://localhost:8080/actuator

## Troubleshooting
- Check logs for connection issues with Config Server or Eureka
- Verify Redis is running if rate limiting is not working
- Ensure JWT secret matches between API Gateway and user-service

## Contributing
1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request
