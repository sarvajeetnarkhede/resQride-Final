# ShopVerse – Intelligent Event-Driven E-Commerce Platform

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

[//]: # ([![Build Status]&#40;https://github.com/yourusername/ShopVerse/actions/workflows/build.yml/badge.svg&#41;]&#40;https://github.com/yourusername/ShopVerse/actions&#41;)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?logo=docker)](https://www.docker.com/)

## 🚀 Overview

ShopVerse is a modern, data-driven e-commerce platform built with a microservices architecture. It leverages Spring Boot, Kafka, and React to deliver real-time analytics, personalized recommendations, and automated batch product processing in a scalable, event-driven ecosystem.

## ✨ Key Features

- **Microservices Architecture**: Independently deployable services with dedicated databases
- **Event-Driven Design**: Real-time event processing with Apache Kafka
- **User Authentication**: Secure JWT-based authentication
- **Product Management**: Bulk product ingestion with real-time job tracking
- **Payment Processing**: Integrated Razorpay payment gateway
- **Real-time Analytics**: Live dashboards with business insights
- **Personalized Recommendations**: AI/ML-powered product suggestions
- **Notification System**: Real-time user notifications
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
  end
 subgraph GATEWAY["Gateway"]
    direction TB
        API["API Gateway"]
  end
 subgraph SERVICES["Services"]
    direction TB
        Auth["Auth Service"]
        User["User Service"]
        Product["Product Service"]
        Order["Order Service"]
        Payment["Payment Service"]
  end
 subgraph EVENTBUS["Event Bus"]
    direction TB
        Kafka[("Kafka Event Bus")]
  end
 subgraph CONSUMERS["Events Consumer"]
    direction TB
        Analytics["Analytics Service"]
        Notification["Notification Service"]
        Recommendation["Recommendation Service"]
  end
 subgraph DATASTORES["Data Store"]
    direction TB
        MySQL[("MySQL")]
        PostgreSQL[("PostgreSQL")]
        MongoDB[("MongoDB")]
        ClickHouse[("ClickHouse")]
        Redis[("Redis Cache")]
  end
 subgraph OBSERVABILITY["Observability"]
    direction TB
        Prometheus["Prometheus"]
        Grafana["Grafana"]
        AdminServer["Spring Boot Admin"]
  end
    CLIENT --> GATEWAY
    GATEWAY --> SERVICES
    SERVICES --> EVENTBUS
    EVENTBUS --> CONSUMERS
    User -- MySQL --> MySQL
    Auth -- MySQL --> MySQL
    Product -- MySQL --> MySQL
    Order -- MySQL --> MySQL
    Payment -- PostgreSQL --> PostgreSQL
    Notification -- MongoDB --> MongoDB
    Recommendation -- MongoDB --> MongoDB
    Analytics -- ClickHouse --> ClickHouse
    Recommendation -- Cache --> Redis
    API -- RateLimit/TokenCache --> Redis
    Kafka -- Offset/Cache --> Redis
    OBSERVABILITY -.-> GATEWAY & SERVICES & EVENTBUS & CONSUMERS & DATASTORES

     Web:::client
     API:::gateway
     Auth:::service
     User:::service
     Product:::service
     Order:::service
     Payment:::service
     Kafka:::eventbus
     Analytics:::consumer
     Notification:::consumer
     Recommendation:::consumer
     MySQL:::database
     PostgreSQL:::client
     MongoDB:::database
     ClickHouse:::database
     Redis:::database
     Prometheus:::monitor
     Grafana:::monitor
     AdminServer:::monitor
    classDef gateway fill:#388e3c,stroke:#1b5e20,stroke-width:2px,color:#fff
    classDef service fill:#7e57c2,stroke:#4527a0,stroke-width:2px,color:#fff
    classDef eventbus fill:#ef6c00,stroke:#e65100,stroke-width:2px,color:#fff
    classDef consumer fill:#00897b,stroke:#004d40,stroke-width:2px,color:#fff
    classDef database fill:#263238,stroke:#789262,stroke-width:2px,color:#fff
    classDef monitor fill:#37474F,stroke:#13181b,stroke-width:2px,color:#fff
    classDef client fill:#1565c0, stroke:#002f6c, stroke-width:2px, color:#fff
    style API fill:#00C853
    style Kafka stroke:#FFFFFF
    style MySQL fill:#AA00FF
    style PostgreSQL fill:#2962FF,stroke-width:4px,stroke-dasharray: 0
    style MongoDB fill:#00C853,stroke:#424242
    style ClickHouse fill:#FFD600,stroke:#424242,color:#000000
    style Redis fill:#D50000,stroke:#FFFFFF,stroke-width:2px,stroke-dasharray: 0
    style Prometheus fill:#2962FF,stroke:#FFFFFF
    style Grafana fill:#2962FF,stroke:#FFFFFF
    style AdminServer fill:#2962FF,stroke:#FFFFFF
    style CLIENT stroke:#1565c0,stroke-width:3px
    style GATEWAY stroke:#388e3c,stroke-width:3px
    style SERVICES stroke:#7e57c2,stroke-width:3px
    style EVENTBUS stroke:#ef6c00,stroke-width:3px,fill:#FF6D00,color:#FFFFFF
    style CONSUMERS stroke:#00897b,stroke-width:3px
    style OBSERVABILITY stroke:#2962FF,stroke-width:3px,fill:#2962FF,color:#FFFFFF
    style DATASTORES stroke:#E1BEE7,stroke-width:3px,fill:transparent,color:#424242
    linkStyle 1 stroke:#00C853,fill:none
    linkStyle 2 stroke:#AA00FF,fill:none
    linkStyle 3 stroke:#FF6D00,fill:none
    linkStyle 4 stroke:#AA00FF,fill:none
    linkStyle 5 stroke:#AA00FF,fill:none
    linkStyle 6 stroke:#AA00FF,fill:none
    linkStyle 7 stroke:#AA00FF,fill:none
    linkStyle 8 stroke:#AA00FF,fill:none
    linkStyle 13 stroke:#00C853,fill:none
    linkStyle 14 stroke:#FF6D00,fill:none
    linkStyle 15 stroke:#2962FF,fill:none
    linkStyle 16 stroke:#2962FF,fill:none
    linkStyle 17 stroke:#2962FF,fill:none
    linkStyle 18 stroke:#2962FF,fill:none
    linkStyle 19 stroke:#2962FF,fill:none
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
- Git

### One-Command Setup

1. **Clone and prepare the repository**
   ```bash
   git clone https://github.com/yourusername/ShopVerse.git
   cd ShopVerse-Backend
   ```

2. **Run the setup script**
   ```bash
   # Make the script executable
   chmod +x scripts/setup.sh
   
   # Run the setup script
   ./scripts/setup.sh
   ```

   The setup script will automatically:
   - Start all required infrastructure (Kafka, ClickHouse, PostgreSQL, MySQL, Redis, MongoDB)
   - Build all microservices
   - Start services in the correct order with proper health checks
   - Display the status of all services

3. **Access the applications**
   - API Gateway: http://localhost:8080
   - Eureka Dashboard: http://localhost:8761
   - Admin Dashboard: http://localhost:8080/admin

### Manual Setup (Alternative)

If you prefer to run services manually:

```bash
# 1. Start infrastructure
cd ShopVerse-Backend
docker-compose up -d kafka zookeeper mysql redis mongodb

# 2. Build and start services (in separate terminals)
cd discovery-service && ./gradlew bootRun
cd ../gateway && ./gradlew bootRun
# Repeat for other services...
```

## 🛠️ Available Services

| Service                    | Port | Description                             |
|----------------------------|------|-----------------------------------------|
| **api-gateway**            | 8080 | API Gateway (Spring Cloud Gateway)      |
| **discovery-service**      | 8761 | Service Registry (Eureka)               |
| **admin-server**           | 8079 | Spring Boot Admin (Monitering) |
| **auth-service**           | 8081 | Authentication & Authorization          |
| **user-service**           | 8082 | User Management                         |
| **product-service**        | 8083 | Product Catalog                         |
| **order-service**          | 8084 | Order Processing                        |
| **payment-service**        | 8085 | Payment Processing                      |
| **notification-service**   | 8086 | Real-time Notifications                 |
| **analytics-service**      | 8087 | Business Analytics                      |
| **recommendation-service** | 8088 | Product Recommendations                 |

## 🔧 Troubleshooting

### Common Issues

1. **Port conflicts**
   - Ensure no other services are running on the required ports (8080, 8761, etc.)
   - Check running containers: `docker ps`

2. **Setup script fails**
   - Make sure Docker is running
   - Check available disk space
   - Increase Docker memory allocation if needed
   - View logs in `logs/` directory

3. **Service not starting**
   - Check service logs in `logs/service-name.log`
   - Verify database connection
   - Ensure Kafka is running: `docker-compose ps kafka`

### Logs

View logs for all services in the `logs/` directory:

```bash
# View logs for a specific service
tail -f logs/gateway.log

# View all logs
tail -f logs/*.log
```

## ⚙️ Configuration

### Environment Variables

The application is pre-configured with sensible defaults. To customize, create a `.env` file in the root directory:

```env
# Database (MySQL)
MYSQL_ROOT_PASSWORD=rootpass
MYSQL_DATABASE=shopverse
MYSQL_USER=shopuser
MYSQL_PASSWORD=shoppass

# JWT Authentication
JWT_SECRET=change_this_to_a_secure_secret
JWT_EXPIRATION=86400000  # 24 hours

# Kafka
KAFKA_BOOTSTRAP_SERVERS=kafka:9092

# Redis
REDIS_HOST=redis
REDIS_PORT=6379

# MongoDB
MONGO_URI=mongodb://mongodb:27017/shopverse

# Razorpay (for payment-service)
RAZORPAY_KEY_ID=your_razorpay_key
RAZORPAY_KEY_SECRET=your_razorpay_secret
```

> **Note**: The setup script will automatically create this file with default values if it doesn't exist.

## 🧪 Testing

Run tests for all services:

```bash
# Run tests for all services
./gradlew test

# Run tests for a specific service
cd <service-directory>
./gradlew test
```

## 🐳 Docker Deployment

Build and run the entire application stack:

```bash
docker-compose up -d --build
```

## 📈 Monitoring

- **Spring Boot Actuator**: http://localhost:8080/actuator
- **Spring Boot Admin**: http://localhost:8079
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (default: admin/admin)

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Spring Boot Team
- Apache Kafka
- Docker Community
- All open-source contributors

---

<div align="center">
  Made with ❤️ by ShopVerse Team | 2025-26
</div>
