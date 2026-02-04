# resQride – Roadside Assistance Platform

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?logo=docker)](https://www.docker.com/)

## 🚀 Overview

resQride is a comprehensive roadside assistance platform built with a microservices architecture. It connects users in need of vehicle assistance with nearby mechanics and service centers, providing real-time tracking, secure payments, and efficient service management.

## ✨ Key Features

- **Microservices Architecture**: Independently deployable services with dedicated databases
- **Real-time Location Services**: GPS-based mechanic and service center discovery
- **User Authentication**: Secure JWT-based authentication with role-based access
- **Service Request Management**: Create, track, and manage roadside assistance requests
- **Mechanic Network**: Registered mechanics with skills, availability, and ratings
- **Payment Processing**: Integrated Razorpay payment gateway for service fees
- **Real-time Notifications**: SMS and in-app notifications for request updates
- **Admin Dashboard**: Comprehensive management interface for operations
- **Request Logging**: Complete audit trail of all API requests and activities
- **Containerized Deployment**: Docker and Docker Compose support

## 🏗️ System Architecture

```mermaid
---
config:
  layout: fixed
---
flowchart TB
 subgraph CLIENT["Client"]
    direction TB
        Web["Web Client"]
        Mobile["Mobile App"]
  end
 subgraph GATEWAY["Gateway"]
    direction TB
        API["API Gateway"]
        Logger["Logger Service"]
  end
 subgraph SERVICES["Services"]
    direction TB
        Auth["Auth Service"]
        User["User Service"]
        Request["Service Request Service"]
        Mechanic["Mechanic Service"]
        Payment["Payment Service"]
        Location["Location Service"]
        Feedback["Feedback Service"]
        Admin["Admin Service"]
  end
 subgraph DATASTORES["Data Store"]
    direction TB
        MySQL[("MySQL")]
        PostgreSQL[("PostgreSQL")]
        MongoDB[("MongoDB")]
        Redis[("Redis Cache")]
  end
 sublog FILES["Log Files"]
    direction TB
        LogFiles[("Daily Logs")]
  end
    CLIENT --> GATEWAY
    GATEWAY --> SERVICES
    GATEWAY --> Logger
    Logger --> LogFiles
    User -- MySQL --> MySQL
    Auth -- MySQL --> MySQL
    Request -- PostgreSQL --> PostgreSQL
    Mechanic -- MySQL --> MySQL
    Payment -- PostgreSQL --> PostgreSQL
    Feedback -- MongoDB --> MongoDB
    Location -- Cache --> Redis
    API -- RateLimit/TokenCache --> Redis

     Web:::client
     Mobile:::client
     API:::gateway
     Logger:::gateway
     Auth:::service
     User:::service
     Request:::service
     Mechanic:::service
     Payment:::service
     Location:::service
     Feedback:::service
     Admin:::service
     MySQL:::database
     PostgreSQL:::database
     MongoDB:::database
     Redis:::database
     LogFiles:::files
    classDef gateway fill:#388e3c,stroke:#1b5e20,stroke-width:2px,color:#fff
    classDef service fill:#7e57c2,stroke:#4527a0,stroke-width:2px,color:#fff
    classDef database fill:#263238,stroke:#789262,stroke-width:2px,color:#fff
    classDef files fill:#ff6f00,stroke:#e65100,stroke-width:2px,color:#fff
    classDef client fill:#1565c0, stroke:#002f6c, stroke-width:2px, color:#fff
    style API fill:#00C853
    style Logger fill:#FF6D00
    style MySQL fill:#AA00FF
    style PostgreSQL fill:#2962FF,stroke-width:4px,stroke-dasharray: 0
    style MongoDB fill:#00C853,stroke:#424242
    style Redis fill:#D50000,stroke:#FFFFFF,stroke-width:2px,stroke-dasharray: 0
    style LogFiles fill:#FF6D00,stroke:#FFFFFF
    style CLIENT stroke:#1565c0,stroke-width:3px
    style GATEWAY stroke:#388e3c,stroke-width:3px
    style SERVICES stroke:#7e57c2,stroke-width:3px
    style DATASTORES stroke:#E1BEE7,stroke-width:3px,fill:transparent,color:#424242
    style FILES stroke:#ff6f00,stroke-width:3px
    linkStyle 1 stroke:#00C853,fill:none
    linkStyle 2 stroke:#AA00FF,fill:none
    linkStyle 3 stroke:#FF6D00,fill:none
```

### Architecture Components

1. **Client Layer**
   - Web and Admin interfaces
   - Communicates via HTTPS with API Gateway

2. **API Gateway**
   - Single entry point for all client requests
   - Request routing, load balancing, and security
   - Caching with Redis

3. **Core Services**
   - **Auth Service**: JWT-based authentication & authorization
   - **User Service**: User profiles and management
   - **Product Service**: Product catalog and inventory
   - **Order Service**: Order processing and management
   - **Payment Service**: Payment processing integration

4. **Event-Driven Services**
   - **Notification Service**: Real-time user notifications
   - **Analytics Service**: Business intelligence and reporting
   - **Recommendation Service**: Personalized product suggestions

5. **Data Layer**
   - **MySQL**: Core transactional data (users, orders)
   - **PostgreSQL**: Payment transactions and financial data
   - **MongoDB**: Product catalog and user profiles (flexible schema)
   - **ClickHouse**: High-performance analytics and reporting
   - **Redis**: Caching, session management, and rate limiting
   - **Elasticsearch**: Product search and recommendations

6. **Monitoring & Operations**
   - Spring Boot Admin for service monitoring
   - Prometheus for metrics collection
   - Grafana for visualization

7. **Message Broker**
   - Apache Kafka for event streaming between services
   - Enables loose coupling and scalability

## 🛠️ Tech Stack

- **Backend**: Java 21, Spring Boot 3.x +, Spring Cloud
- **Frontend**: React.js, Redux, Tailwind CSS
- **Database**: MySQL, MongoDB, PostgreSQL, ClickHouse, Redis (Caching)
- **Message Broker**: Apache Kafka, Apache Flink
- **Service Discovery**: Spring Cloud Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Authentication**: JWT, Spring Security
- **Payment Processing**: Razorpay Integration
- **Containerization**: Docker, Docker Compose
- **CI/CD**: GitHub Actions
- **Monitoring**: Spring Boot Actuator, Spring Boot Admin, Prometheus, Grafana

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose
- Java 21 or higher
- .NET 8.0 SDK (for logger service)
- Git

### One-Command Setup
```bash
git clone <repository-url>
cd resQride-Final
chmod +x scripts/setup.sh
./scripts/setup.sh
```

### Access Points
- API Gateway: http://localhost:8080
- Logger Service: http://localhost:9090
- Eureka Dashboard: http://localhost:8761
- Admin Dashboard: http://localhost:8080/admin

## 🛠️ Available Services

| Service | Port | Technology | Description |
|---------|------|------------|-------------|
| api-gateway | 8080 | Spring Boot | API Gateway & Routing |
| discovery-service | 8761 | Spring Boot | Service Registry |
| auth-service | 8081 | Spring Boot | Authentication |
| user-service | 8082 | Spring Boot | User Management |
| service-request | 8083 | Spring Boot | Service Requests |
| mechanic-service | 8084 | Spring Boot | Mechanic Network |
| payment-service | 8085 | Spring Boot | Payment Processing |
| location-service | 8086 | Spring Boot | Location Services |
| feedback-service | 8087 | Spring Boot | Feedback & Ratings |
| admin-service | 8088 | Spring Boot | Admin Operations |
| logger-service | 9090 | .NET 8 | API Request Logging |

## 📊 Logger Service

### Features
- Real-time API request logging
- Timestamp tracking
- Response time monitoring
- User attribution
- Request ID tracking

### API Endpoints
- `GET /api/logger/health` - Health check
- `GET /api/logger/logs` - View logs
- `GET /api/logger/services` - Active services

### Log Format
```
[2026-02-02 17:38:53.584] GET /api/mechanics/centers - 200 - gateway - 192.168.1.100 - Mozilla/5.0... - 150ms - User:user@example.com - Req:a1b2c3d4
```

## 🔧 Troubleshooting

### Common Issues
1. **Port conflicts** - Check `docker ps`
2. **Logger service** - Test: `curl http://localhost:9090/api/logger/health`
3. **Gateway logging** - Check connectivity between gateway and logger

### Logs
```bash
# View API logs
tail -f logs/$(date +%Y-%m-%d).log

# View service logs
docker logs <service-name>
```

## 🐳 Docker Deployment
```bash
# Start all services
docker-compose up -d --build

# Start specific services
docker-compose up -d logger-service gateway
```

## 📈 Monitoring
- Logger Health: http://localhost:9090/api/logger/health
- API Gateway: http://localhost:8080/actuator/health
- Service Registry: http://localhost:8761

## 🤝 Contributing
1. Fork the repository
2. Create feature branch
3. Commit changes
4. Push to branch
5. Open Pull Request

## 📄 License
MIT License - see [LICENSE](LICENSE) file

---

<div align="center">
  Made with ❤️ by resQride Team | 2025-26
</div>
