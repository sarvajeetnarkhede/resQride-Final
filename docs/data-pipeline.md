# 📊 ShopVerse – Unified Data Pipeline

This document describes the **end-to-end data pipeline** of the ShopVerse platform.
All microservices communicate asynchronously using **Kafka**, enabling
**notifications, analytics, and recommendations** without tight coupling.

---

## 🧠 Design Goals

- Event-driven architecture
- Loose coupling between services
- Real-time analytics & personalization
- Horizontal scalability
- Fault tolerance & replayability

---

## 🏗️ High-Level Data Pipeline

```mermaid
flowchart LR
    Auth["Auth Service"] -- USER_EVENTS --> Kafka[("Kafka Topics")]
    User["User Service"] -- USER_EVENTS --> Kafka
    Product["Product Service"] -- PRODUCT_EVENTS --> Kafka
    Order["Order Service"] -- ORDER_EVENTS --> Kafka
    Payment["Payment Service"] -- PAYMENT_EVENTS --> Kafka
    Kafka --> Notification["Notification Service"] & Analytics["Analytics Service"] & Recommendation["Recommendation Service"]
    Notification --> Mongo[("MongoDB")]
    Analytics --> ClickHouse[("ClickHouse")]
    Recommendation --> Redis[("Redis Cache")]

     Auth:::producer
     Kafka:::queue
     User:::producer
     Product:::producer
     Order:::producer
     Payment:::producer
     Notification:::consumer
     Analytics:::consumer
     Recommendation:::consumer
     Mongo:::storage
     ClickHouse:::storage
     Redis:::storage
    classDef producer fill:#4f8cff,stroke:#000,color:#fff
    classDef consumer fill:#7fd1ae,stroke:#000,color:#000
    classDef queue fill:#ffb347,stroke:#000,color:#000
    classDef storage fill:#d4a5ff,stroke:#000,color:#000
````

---

## 🔄 Event Flow Explanation

### 1️⃣ Event Producers (Core Services)

Each core service publishes **domain events**:

* `Auth Service` → USER_LOGIN, USER_REGISTER
* `Product Service` → PRODUCT_CREATED, PRODUCT_VIEWED
* `Order Service` → ORDER_PLACED, ORDER_CANCELLED
* `Payment Service` → PAYMENT_SUCCESS, PAYMENT_FAILED

These events are sent to Kafka topics such as:

* `user-events`
* `product-events`
* `order-events`
* `payment-events`

---

### 2️⃣ Kafka – Event Backbone

Kafka acts as:

* Central event bus
* Message buffer
* Replay mechanism
* Scalability layer

**Multiple services can consume the same event independently.**

---

### 3️⃣ Notification Service

* Listens to all business events
* Converts events into **user/admin notifications**
* Stores data in **MongoDB**
* API behavior:

    * **Admin** → sees all notifications
    * **User** → sees only own notifications

---

### 4️⃣ Analytics Service

* Consumes all events
* Processes data via **Flink**
* Stores metrics in **ClickHouse**
* Provides dashboards:

    * Total users
    * Orders per day
    * Revenue
    * Conversion rates

---

### 5️⃣ Recommendation Service

* Consumes **user behavior events**
* Updates user-item interaction models
* Uses **Redis** for low-latency recommendations
* Supports:

    * Popular items
    * Collaborative filtering
    * Content-based filtering

---

## 🧩 Why This Architecture Works

| Feature      | Benefit                         |
| ------------ | ------------------------------- |
| Kafka        | Loose coupling & scalability    |
| Event replay | Debugging & reprocessing        |
| MongoDB      | Flexible notification storage   |
| ClickHouse   | Ultra-fast analytics            |
| Redis        | Sub-millisecond recommendations |

---

## 🚀 Summary

ShopVerse uses a **single unified data pipeline** where:

* Core services **produce events**
* Specialized services **consume and act**
* No service directly depends on another
* The system is scalable, fault-tolerant, and extensible

This design is **production-grade** and **industry-standard**.
