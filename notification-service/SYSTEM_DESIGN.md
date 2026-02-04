# System Design: Real-Time Notification Service

A scalable, distributed real-time notification system built with Spring Boot, Redis, and PostgreSQL.

## Architecture Overview

The system is designed to handle thousands of concurrent WebSocket connections across multiple server instances. Key components include:

- **Protocol**: WebSocket (STOMP) for low-latency, bidirectional communication.
- **Message Broker**: **Redis Pub/Sub** ensures that a notification sent to one server is broadcast to all other instances, allowing users to receive notifications regardless of which server they are connected to.
- **Load Balancer**: **Nginx** acts as the entry point, distributing traffic between application instances.
- **Persistence**: **PostgreSQL** stores notification history for offline retrieval.
- **Service Discovery**: Docker Compose handles internal DNS, allowing components to communicate via service names (e.g., `redis`, `postgres`).

## Key Design Decisions

### 1. Redis for Distributed Pub/Sub
In a multi-server environment, a standard in-memory message broker only reaches users connected to that specific instance. By using Redis:
- **Scalability**: We can spin up $N$ instances of the application.
- **Consistency**: Messages are globally broadcast using Redis topics, ensuring delivery in a distributed environment.

### 2. Nginx with Sticky Sessions (`ip_hash`)
WebSockets require a persistent handshake. Using `ip_hash` ensures that:
- A client is always routed to the same server instance during a session.
- This prevents "handshake errors" that occur if a server receiving the WebSocket upgrade request doesn't have the initial HTTP session state.

### 3. PostgreSQL persistence
While notifications are transient, having a persistent store allows for "unread notification" features and reliability if the message broker is temporarily unavailable.

## Performance & Scalability

| Component | Role | Capacity (Est.) | Primary Bottleneck |
| :--- | :--- | :--- | :--- |
| **Spring Boot** | Application Logic | 10K+ connections / server | File Descriptors / RAM |
| **Redis** | Message Broker | 100K+ messages / sec | Network throughput |
| **PostgreSQL** | Database | 5K+ writes / sec | Disk I/O |
| **Nginx** | Reverse Proxy | 50K+ requests / sec | CPU for SSL/TLS |

## How to Run locally

```bash
docker-compose up --build
```
Once running, the application is accessible at `http://localhost`. Nginx will handle the routing to either `app1` (8080) or `app2` (8081).
