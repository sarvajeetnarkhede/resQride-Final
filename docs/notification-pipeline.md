# 🔔 Notification Pipeline – ShopVerse

This document explains how **notifications are generated, processed, stored, and served**
in the ShopVerse platform using an **event-driven architecture**.

---

## 🎯 Purpose of Notification Service

The Notification Service is responsible for:
- Capturing important business events
- Generating user/admin notifications
- Storing notifications reliably
- Serving notifications via REST APIs

This service **does NOT** block or affect core business logic.

---

## 🏗️ Notification Pipeline Architecture

```mermaid
flowchart LR
    %% Producers
    Auth[Auth Service]
    User[User Service]
    Product[Product Service]
    Order[Order Service]
    Payment[Payment Service]

    %% Event Bus
    Kafka[(Kafka)]

    %% Notification
    Notification[Notification Service]
    Mongo[(MongoDB)]

    %% Flow
    Auth -->|USER_EVENTS| Kafka
    User -->|USER_EVENTS| Kafka
    Product -->|PRODUCT_EVENTS| Kafka
    Order -->|ORDER_EVENTS| Kafka
    Payment -->|PAYMENT_EVENTS| Kafka

    Kafka --> Notification
    Notification --> Mongo

    %% Classes for colorful nodes
    classDef authService fill:#A5D6A7,stroke:#388E3C,stroke-width:2px,color:#1B5E20;
    classDef userService fill:#90CAF9,stroke:#1565C0,stroke-width:2px,color:#0D47A1;
    classDef productService fill:#FFD54F,stroke:#FFA000,stroke-width:2px,color:#FF6F00;
    classDef orderService fill:#FFB74D,stroke:#F57C00,stroke-width:2px,color:#E65100;
    classDef paymentService fill:#EF9A9A,stroke:#C62828,stroke-width:2px,color:#B71C1C;
    classDef kafka fill:#B2DFDB,stroke:#00897B,stroke-width:2px,color:#004D40;
    classDef notificationService fill:#CE93D8,stroke:#6A1B9A,stroke-width:2px,color:#4A148C;
    classDef mongo fill:#FFF176,stroke:#FBC02D,stroke-width:2px,color:#F57F17;

    class Auth authService;
    class User userService;
    class Product productService;
    class Order orderService;
    class Payment paymentService;
    class Kafka kafka;
    class Notification notificationService;
    class Mongo mongo;
````

---

## 🔄 Event Flow (Step-by-Step)

### 1️⃣ Event Production (Other Services)

Core services publish **notification-worthy events** to Kafka:

| Service | Event Type                      |
| ------- | ------------------------------- |
| Auth    | USER_LOGIN, USER_REGISTER       |
| Product | PRODUCT_CREATED                 |
| Order   | ORDER_PLACED, ORDER_CANCELLED   |
| Payment | PAYMENT_SUCCESS, PAYMENT_FAILED |

Example event:

```json
{
  "eventType": "ORDER_PLACED",
  "userEmail": "user@email.com",
  "message": "Order placed successfully",
  "timestamp": "2026-01-02T10:15:30"
}
```

---

### 2️⃣ Kafka Topics

Notification Service listens to multiple topics:

```text
user-events
product-events
order-events
payment-events
```

Kafka ensures:

* Message durability
* Parallel consumption
* Replay capability

---

### 3️⃣ Notification Consumer

The Notification Service:

* Deserializes Kafka events
* Converts them to `Notification` documents
* Stores them in MongoDB

```java
@KafkaListener(
    topics = {
        "user-events",
        "order-events",
        "payment-events",
        "product-events"
    },
    groupId = "notification-group"
)
public void consume(byte[] payload) {
    NotificationEvent event =
        objectMapper.readValue(payload, NotificationEvent.class);

    Notification notification = Notification.builder()
        .userEmail(event.getUserEmail())
        .eventType(event.getEventType())
        .message(event.getMessage())
        .timestamp(event.getTimestamp())
        .read(false)
        .build();

    repository.save(notification);
}
```

---

### 4️⃣ Notification Storage (MongoDB)

Each notification is stored as a document:

```json
{
  "_id": "abc123",
  "eventType": "ORDER_PLACED",
  "userEmail": "user@email.com",
  "message": "Order placed successfully",
  "timestamp": "2026-01-02T10:15:30",
  "read": false
}
```

MongoDB is used because:

* Flexible schema
* High write throughput
* Easy querying per user

---

## 🔐 Access Control Logic

Notifications are filtered **at API level**, not Kafka.

| Role  | Access                 |
| ----- | ---------------------- |
| ADMIN | All notifications      |
| USER  | Only own notifications |

```java
@GetMapping
public List<Notification> getNotifications(
    @RequestHeader("X-User-Email") String email,
    @RequestHeader("X-User-Role") String role
) {
    if ("ADMIN".equals(role)) {
        return repository.findAllByOrderByTimestampDesc();
    }
    return repository.findByUserEmailOrderByTimestampDesc(email);
}
```

---

## ⚡ Why This Design Is Correct

| Feature           | Benefit                    |
| ----------------- | -------------------------- |
| Kafka             | Non-blocking notifications |
| MongoDB           | Scalable storage           |
| Event-driven      | No service coupling        |
| Header-based RBAC | Gateway-friendly           |
| Multiple topics   | Fine-grained control       |

---

## 🚀 Advantages

* Notifications never block core services
* Easy to add new notification types
* Supports retries & DLQ (future)
* Admin & user views handled cleanly
* Horizontally scalable

---

## 🧠 Interview Explanation (Short)

> “Every service emits domain events to Kafka.
> The Notification Service listens to those events, stores them in MongoDB, and exposes role-based APIs for users and admins.
> This keeps notifications asynchronous, scalable, and completely decoupled from business logic.”

---

## 🔮 Future Enhancements

* WebSocket / SSE for real-time notifications
* Push notifications (FCM)
* Email/SMS adapters
* Dead-Letter Queue (DLQ)
* Read/unread indexing optimization
