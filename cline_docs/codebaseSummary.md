# Tổng Quan Mã Nguồn

## Cấu Trúc Dự Án
```
chat-app/
├── frontend/            # React application
│   ├── src/
│   │   ├── components/
│   │   │   ├── layout/
│   │   │   └── ui/
│   │   ├── lib/
│   │   └── styles/
├── backend/
│   └── services/
│       ├── service-registry/    # Eureka Server
│       ├── api-gateway/         # Spring Cloud Gateway
│       ├── user-service/        # User management
│       └── messaging-service/   # Chat functionality
└── cline_docs/         # Project documentation
```

## Components Chính

### Frontend (Telegram Style)
- React + Vite
- Tailwind CSS
- WebSocket client
- Telegram-like UI components

### Backend Services

#### Service Registry (Eureka Server)
- Service discovery
- Load balancing
- Port: 8761

#### API Gateway
- Route management
- Security
- Port: 8080

#### User Service
- User management
- Authentication
- PostgreSQL database
- Port: 8081

#### Messaging Service
- Real-time messaging
- WebSocket
- MongoDB database
- Port: 8082

## Database Schema

### PostgreSQL (User Service)
- Users table
  - UUID primary key
  - Username (unique)
  - Email (unique)
  - Password (hashed)
  - Avatar URL
  - Timestamps

### MongoDB (Messaging Service)
- Messages collection
  - Text/file messages
  - Sender/receiver info
  - Room reference
  - Timestamps

- ChatRooms collection
  - Group/private chats
  - Member list
  - Room settings
  - Timestamps

## API Endpoints

### User Service
- POST /api/users - Create user
- GET /api/users/{id} - Get user
- PUT /api/users/{id} - Update user
- DELETE /api/users/{id} - Delete user

### Messaging Service
- WebSocket: /ws
- REST:
  - /api/rooms/* - Room management
  - /api/messages/* - Message operations

## Dependencies
### Frontend
- React + Vite
- Tailwind CSS
- Socket.IO Client

### Backend
- Spring Boot 3.2.0
- Spring Cloud 2023.0.0
- PostgreSQL
- MongoDB
- WebSocket
- JWT

## Thay Đổi Gần Đây
- Khởi tạo microservices architecture
- Cài đặt service registry và API gateway
- Triển khai user service với PostgreSQL
- Triển khai messaging service với MongoDB và WebSocket
- Cấu hình giao tiếp giữa các services

## Kế Hoạch Tiếp Theo
- Triển khai search service
- Tích hợp file sharing
- Cài đặt monitoring
- Triển khai CI/CD