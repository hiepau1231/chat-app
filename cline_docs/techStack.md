# Technology Stack & Architecture Decisions

## Backend Technologies

### Core Framework & Libraries
- Spring Boot 3.2.0
  - Lựa chọn cho khả năng mở rộng và hiệu suất cao
  - Hỗ trợ tốt cho microservices architecture
  - Tích hợp sẵn nhiều công cụ monitoring và security

### Authentication & Authorization
- Spring Security
  - Custom UserDetailsService implementation
  - JWT based authentication
  - @AuthenticationPrincipal annotation cho user context
- JWT Implementation
  - Access token & Refresh token mechanism
  - Token refresh mỗi 14 phút
  - Secure token storage

### Microservices Infrastructure
- Spring Cloud
  - Eureka Server cho service discovery
  - Gateway cho routing và load balancing
  - Circuit breaker pattern với Resilience4j

### Database Solution
- MongoDB
  - Database chính cho toàn bộ dự án
  - Collections:
    - users: User data và profile
    - chat_rooms: Room information
    - messages: Chat messages
  - Indexes:
    - users.username (unique)
    - users.email (unique)
    - messages.roomId
    - messages.createdAt

- Redis
  - Caching layer
  - Session management
  - Rate limiting
  - Token blacklisting

### Search Engine (Planned)
- Elasticsearch
  - Full-text search cho messages
  - User search functionality
  - Analytics và logging

### Message Queue (Planned)
- Apache Kafka
  - Event streaming cho notifications
  - Message broadcasting
  - Service communication

## Frontend Technologies

### Core Framework
- React + TypeScript
  - Type safety với custom types và interfaces
  - Component reusability
  - Performance optimization
  - Custom hooks cho authentication và chat

### Authentication Management
- Context API
  - Centralized auth state
  - Token management
  - Auto refresh mechanism
  - Secure storage handling

### State Management
- Redux Toolkit
  - Centralized state management
  - Predictable state updates
  - DevTools integration
  - Custom slices cho auth và chat

### Real-time Communication
- Socket.IO
  - WebSocket wrapper
  - Fallback mechanisms
  - Room management
  - Real-time status updates

### UI Components
- Material UI + TailwindCSS
  - Consistent design system
  - Responsive components
  - Custom styling với Tailwind
  - Form handling và validation

## DevOps & Infrastructure

### Containerization
- Docker
  - Service isolation
  - Consistent environments
  - Easy deployment
  - Multi-stage builds

### Orchestration (Planned)
- Kubernetes
  - Container orchestration
  - Auto-scaling
  - Service discovery
  - Config management

### Monitoring Stack
- Prometheus
  - Metrics collection
  - Alert management
  - Performance monitoring
  - Custom metrics

- Grafana
  - Visualization
  - Dashboard creation
  - Alert configuration
  - Real-time monitoring

### Logging
- ELK Stack
  - Elasticsearch cho storage
  - Logstash cho processing
  - Kibana cho visualization
  - Custom log formats

## Security Implementation

### Authentication Flow
1. Client-side:
   - Form validation
   - Secure token storage
   - Auto refresh mechanism
   - Error handling

2. Server-side:
   - JWT generation và validation
   - Role-based access control
   - Token refresh logic
   - Security filters

### API Security
- Spring Security
  - Authentication filters
  - Authorization rules
  - CORS configuration
  - XSS protection

## Architecture Decisions

### Microservices Separation
1. User Service
   - User management
   - Authentication
   - Profile handling
   - Friend system

2. Message Service
   - Chat functionality
   - Real-time messaging
   - Room management
   - Message persistence

3. Notification Service (Planned)
   - Push notifications
   - Email notifications
   - Alert management
   - Notification preferences

4. Search Service (Planned)
   - Full-text search
   - User search
   - Analytics
   - Search optimization

### Communication Patterns
- REST APIs cho synchronous communication
- WebSocket cho real-time features
- Event-driven cho notifications (planned)
- Service mesh cho inter-service communication

### Data Management
- MongoDB cho toàn bộ data storage
- Redis cho caching và rate limiting
- Event sourcing cho audit trails
- Data replication cho high availability

## Scalability Considerations
- Horizontal scaling cho microservices
- Database sharding cho high throughput
- CDN integration cho static content
- Load balancing cho traffic distribution
- Auto-scaling policies
- Cache strategies
- Connection pooling
- Query optimization

## Data Models

### User Model
```json
{
  "_id": ObjectId,
  "username": String,
  "email": String,
  "password": String,
  "role": "ADMIN" | "USER",
  "status": "ONLINE" | "OFFLINE" | "AWAY",
  "createdAt": Date,
  "lastLogin": Date,
  "profile": {
    "avatar": String,
    "bio": String,
    "friends": [String],
    "settings": {
      "emailNotifications": Boolean,
      "pushNotifications": Boolean,
      "theme": String,
      "language": String
    }
  }
}
```

### ChatRoom Model
```json
{
  "_id": ObjectId,
  "name": String,
  "members": [String],
  "createdAt": Date,
  "updatedAt": Date
}
```

### Message Model
```json
{
  "_id": ObjectId,
  "content": String,
  "senderId": String,
  "roomId": String,
  "createdAt": Date
}