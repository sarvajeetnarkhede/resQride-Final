# 📊 Analytics Pipeline – ShopVerse

This document explains how **business events are collected, processed, stored, and queried**
using **Kafka, Apache Flink, and ClickHouse** in the ShopVerse platform.

---

## 🎯 Purpose of Analytics Service

The Analytics Service is responsible for:
- Capturing **system-wide business events**
- Processing events in **real-time**
- Storing analytics data in **ClickHouse**
- Providing **fast analytical queries** for dashboards

Analytics is **read-heavy**, **append-only**, and **event-driven**.

---

## 🏗️ Analytics Pipeline Architecture

```mermaid
---
config:
  layout: fixed
  theme: neo-dark
---
flowchart LR
    Auth["Auth Service"] -- USER_EVENTS --> Kafka[("Kafka")]
    User["User Service"] -- USER_EVENTS --> Kafka
    Product["Product Service"] -- PRODUCT_EVENTS --> Kafka
    Order["Order Service"] -- ORDER_EVENTS --> Kafka
    Payment["Payment Service"] -- PAYMENT_EVENTS --> Kafka
    Kafka --> Flink["Apache Flink"]
    Flink --> ClickHouse[("ClickHouse")]
    AnalyticsAPI["Analytics Service API"] --> ClickHouse

     Auth:::producers
     Kafka:::kafka
     User:::producers
     Product:::producers
     Order:::producers
     Payment:::producers
     Flink:::flink
     ClickHouse:::clickhouse
     AnalyticsAPI:::api
    classDef producers fill:#4fc3f7,stroke:#0288d1,stroke-width:2px
    classDef kafka fill:#ffd54f,stroke:#ff8f00,stroke-width:2px
    classDef flink fill:#f06292,stroke:#c2185b,stroke-width:2px
    classDef clickhouse fill:#aed581,stroke:#558b2f,stroke-width:2px
    classDef api fill:#b39ddb,stroke:#512da8,stroke-width:2px
    style Auth fill:#2962FF
    style Kafka fill:#FFD600,color:#424242
    style User fill:#2962FF
    style Product fill:#2962FF
    style Order fill:#2962FF
    style Payment fill:#2962FF
    style ClickHouse fill:#00C853
    style AnalyticsAPI fill:#AA00FF
````

---

## 🔄 End-to-End Event Flow

### 1️⃣ Event Production (Source Services)

Each service emits **business events** to Kafka when important actions occur.

| Service | Event Type                |
| ------- | ------------------------- |
| Auth    | USER_REGISTER, USER_LOGIN |
| Order   | ORDER_PLACED              |
| Payment | PAYMENT_SUCCESS           |
| Product | PRODUCT_CREATED           |
| User    | PROFILE_UPDATED           |

Example event payload:

```json
{
  "eventType": "ORDER_PLACED",
  "service": "order",
  "userEmail": "user@email.com",
  "entityId": "ORD123",
  "amount": 2499.00,
  "timestamp": "2026-01-02T11:20:30",
  "metadata": {
    "paymentMode": "UPI"
  }
}
```

---

### 2️⃣ Kafka as Event Backbone

Kafka provides:

* Durable event storage
* Replayability
* Parallel consumption
* Decoupling between services

Topic used:

```text
analytics-events
```

Kafka acts as the **source of truth** for analytics data.

---

### 3️⃣ Stream Processing with Apache Flink

Flink:

* Consumes events from Kafka
* Parses JSON into `AnalyticsEvent`
* Performs transformations if needed
* Writes data into ClickHouse

```java
env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source")
    .map(json -> mapper.readValue(json, AnalyticsEvent.class))
    .addSink(new ClickHouseSink());
```

Why Flink?

* Exactly-once semantics
* High throughput
* Low latency
* Real-time aggregation capability

---

### 4️⃣ ClickHouse Storage (OLAP)

ClickHouse is used because:

* Columnar storage
* Extremely fast aggregations
* Handles billions of rows
* Designed for analytics workloads

Table design:

```sql
CREATE TABLE analytics_events (
    event_type String,
    service String,
    user_email String,
    entity_id String,
    amount Float64,
    timestamp DateTime,
    metadata String
)
ENGINE = MergeTree()
ORDER BY (event_type, timestamp);
```

---

### 5️⃣ Analytics Query Layer

Spring Boot service queries ClickHouse using `JdbcTemplate`.

```java
public long totalUsers() {
    return jdbcTemplate.queryForObject("""
        SELECT count()
        FROM analytics_events
        WHERE event_type = 'USER_REGISTER'
    """, Long.class);
}
```

Typical queries:

* Total users
* Total revenue
* Orders per day
* Payments by method
* Conversion metrics

---

## 📈 Dashboard API

Analytics Service exposes read-only APIs:

```http
GET /api/analytics/dashboard
```

Response:

```json
{
  "totalUsers": 1200,
  "totalRevenue": 945000,
  "ordersToday": 87
}
```

---

## 🔐 Security Model

* Analytics APIs are **read-only**
* Protected via API Gateway
* Typically **ADMIN only**

Analytics service never modifies core data.

---

## ⚡ Why This Architecture Works

| Component    | Reason                    |
| ------------ | ------------------------- |
| Kafka        | Decouples producers       |
| Flink        | Real-time processing      |
| ClickHouse   | Ultra-fast analytics      |
| JDBC         | Simple, stable querying   |
| Event-driven | Scalable & fault-tolerant |

---

## 🧠 Interview Explanation (Short)

> “All services emit business events to Kafka.
> Apache Flink processes those events in real-time and stores them in ClickHouse.
> Analytics APIs query ClickHouse to power dashboards without impacting transactional systems.”

---

## 🔮 Future Enhancements

* Windowed aggregations in Flink
* Pre-computed materialized views
* User funnel analysis
* Real-time alerting
* Grafana dashboards on ClickHouse
