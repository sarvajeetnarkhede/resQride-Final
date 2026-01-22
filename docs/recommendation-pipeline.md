# 🎯 Recommendation Pipeline – ShopVerse

This document explains how **personalized recommendations** are generated in ShopVerse using  
**Kafka, real-time user interactions, Redis caching, and hybrid recommendation strategies**.

---

## 🎯 Purpose of Recommendation Service

The Recommendation Service is responsible for:
- Collecting **user interaction events**
- Generating **personalized product recommendations**
- Serving recommendations with **low latency**
- Continuously adapting to user behavior

Unlike Analytics, recommendations are:
- **User-specific**
- **Low-latency**
- **Frequently refreshed**

---

## 🏗️ Recommendation Pipeline Architecture

```mermaid
---
config:
  layout: fixed
---
flowchart LR
    Web["Web Client"] --> API["API Gateway"]
    API --> RecService["Recommendation Service"]
    Product["Product Service"] -- PRODUCT_EVENTS --> Kafka[("Kafka")]
    Order["Order Service"] -- ORDER_EVENTS --> Kafka
    Web -- VIEW / CLICK --> Kafka
    Kafka --> RecService
    RecService --> Redis[("Redis Cache")] & API

     Web:::web
     API:::api
     RecService:::rec
     Product:::prod
     Kafka:::kafka
     Order:::order
     Redis:::redis
    classDef web fill:#4fc3f7,stroke:#0277bd,stroke-width:2px
    classDef api fill:#ffd54f,stroke:#ffa000,stroke-width:2px
    classDef rec fill:#ce93d8,stroke:#6d4c41,stroke-width:2px
    classDef prod fill:#aed581,stroke:#689f38,stroke-width:2px
    classDef order fill:#fff176,stroke:#fbc02d,stroke-width:2px
    classDef kafka fill:#90a4ae,stroke:#37474f,stroke-width:2px
    classDef redis fill:#ef9a9a,stroke:#b71c1c,stroke-width:2px
    style Web fill:#2962FF
    style API fill:#FF6D00
    style RecService fill:#AA00FF
    style Product fill:#00C853
    style Kafka fill:#000000,color:#FFFFFF
    style Order fill:#FFD600
    style Redis fill:#D50000,color:#FFFFFF
````

---

## 🔄 Event Flow Explained

### 1️⃣ User Interaction Events

User behavior events are captured in real-time:

| Action               | Event                |
| -------------------- | -------------------- |
| View product         | PRODUCT_VIEWED       |
| Add to cart          | ADD_TO_CART          |
| Place order          | ORDER_PLACED         |
| Click recommendation | RECOMMENDATION_CLICK |

Example event:

```json
{
  "userEmail": "user@email.com",
  "itemId": "PROD123",
  "eventType": "PRODUCT_VIEWED",
  "timestamp": "2026-01-02T11:30:00"
}
```

These events are sent to **Kafka**.

---

### 2️⃣ Kafka as Recommendation Backbone

Kafka ensures:

* Ordering of user actions
* Replayable interaction history
* Decoupling frontend from recommendation logic

Topics:

```text
user-events
order-events
product-events
```

---

### 3️⃣ Recommendation Engine Processing

The Recommendation Service consumes events and:

* Updates **user-item interaction history**
* Invalidates stale cache
* Triggers real-time recommendation refresh

```java
@KafkaListener(topics = "user-events")
public void consume(UserInteraction event) {
    recommendationEngine.recordInteraction(event);
}
```

---

## 🧠 Recommendation Strategies

### 🔹 Popular Items

* Global popularity
* Best sellers
* Trending products

### 🔹 Collaborative Filtering

* “Users similar to you liked…”
* Based on shared behavior

### 🔹 Content-Based Filtering

* Similar products by category/tags
* User preferences

### 🔹 Hybrid Strategy

Final recommendations are weighted:

```
Final Score = CF * 0.5 + Content * 0.3 + Popular * 0.2
```

---

## ⚡ Redis Caching Layer

Redis stores recommendations per user:

```
recs:{userEmail}:homepage
recs:{userEmail}:product:{productId}
```

Benefits:

* Millisecond response time
* Reduced recomputation
* High scalability

Cache invalidation occurs when:

* User performs a new action
* Order is placed
* Product catalog updates

---

## 🚀 Serving Recommendations

API endpoint:

```http
GET /api/recommendations?context=homepage
```

Response:

```json
[
  { "productId": "PROD12", "score": 0.92 },
  { "productId": "PROD88", "score": 0.88 }
]
```

---

## 🧩 Why Recommendation ≠ Analytics

| Recommendation       | Analytics         |
| -------------------- | ----------------- |
| User-specific        | System-wide       |
| Low latency          | Aggregation-heavy |
| Redis cache          | ClickHouse        |
| Real-time adaptation | Historical trends |
| Personalization      | Reporting         |

---

## 🔐 Security Model

* Uses JWT forwarded via API Gateway
* Recommendations are user-scoped
* No cross-user data leakage

---

## ⚙️ Failure Handling

* Kafka ensures no data loss
* Redis cache fallback
* Graceful degradation to popular items

---

## 🧠 Interview Explanation (Short)

> “User interactions are streamed to Kafka.
> Recommendation Service consumes those events, updates user profiles, caches results in Redis, and serves personalized recommendations in milliseconds.”

---

## 🔮 Future Enhancements

* Session-based recommendations
* ML embeddings
* A/B testing of strategies
* Reinforcement learning
* Real-time personalization
