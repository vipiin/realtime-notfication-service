# Real-time Notification Service
Distributed WebSocket system with Redis Pub/Sub

## Architecture
- Spring Boot 3 (WebSocket/STOMP)
- Redis Pub/Sub (Cross-server messaging)
- PostgreSQL (Message persistence)
- Docker Compose (Multi-instance deployment)

1. The "Group" Lane (Broadcast)
Redis Channel: chat
Behavior: When a message hits this lane, it goes to all servers, and then all users.
Use Case: Public messaging, group notifications, or system-wide announcements.
End Result: Every browser tab "hears" this message.
2. The "Individual" Lane (Targeted)
Redis Channel: notifications
Behavior: When a message hits this lane, it still goes to all servers, but each server performs a check: "Is the target user (e.g., Vipin) connected to me?"
End Result: Only one browser tab "hears" this message.