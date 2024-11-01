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
│           ├── src/main/java/com/chatapp/
│           │   ├── config/
│           │   │   └── WebSocketConfig.java
│           │   ├── controller/
│           │   │   ├── ChatRoomController.java
│           │   │   └── MessageController.java
│           │   ├── model/
│           │   │   ├── ChatRoom.java
│           │   │   └── Message.java
│           │   ├── repository/
│           │   │   ├── ChatRoomRepository.java
│           │   │   └── MessageRepository.java
│           │   └── service/
│           │       ├── ChatRoomService.java
│           │       └── MessageService.java
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
- WebSocket và REST endpoints
- MongoDB Atlas integration
- Port: 8082
- Chức năng:
  * Quản lý phòng chat (group/private)
  * Gửi/nhận tin nhắn real-time
  * Lưu trữ lịch sử chat
  * Quản lý thành viên phòng

## Database Schema

### PostgreSQL (User Service)
- Users table
  - UUID primary key
  - Username (unique)
  - Email (unique)
  - Password (hashed)
  - Avatar URL
  - Timestamps

### MongoDB Atlas (Messaging Service)
- Messages collection
  ```json
  {
    "_id": "ObjectId",
    "content": "String",
    "type": "text/image/file",
    "fileUrl": "String (optional)",
    "fileName": "String (optional)",
    "senderId": "String",
    "receiverId": "String (optional)",
    "roomId": "String",
    "createdAt": "DateTime",
    "updatedAt": "DateTime"
  }
  ```

- ChatRooms collection
  ```json
  {
    "_id": "ObjectId",
    "name": "String",
    "isPrivate": "Boolean",
    "members": ["String (userId)"],
    "ownerId": "String",
    "type": "direct/group",
    "createdAt": "DateTime",
    "updatedAt": "DateTime"
  }
  ```

## API Endpoints

### User Service
- POST /api/users - Create user
- GET /api/users/{id} - Get user
- PUT /api/users/{id} - Update user
- DELETE /api/users/{id} - Delete user

### Messaging Service
- WebSocket: /ws
  - STOMP endpoint: /chat.send
  - Subscriptions:
    * /topic/room/{roomId}
    * /user/{userId}/queue/messages

- REST Endpoints:
  - Messages:
    * POST /api/messages - Tạo tin nhắn mới
    * GET /api/messages/room/{roomId} - Lấy tin nhắn của phòng
    * GET /api/messages/user/{userId} - Lấy tin nhắn của user
    * DELETE /api/messages/{id} - Xóa tin nhắn

  - Chat Rooms:
    * POST /api/rooms - Tạo phòng chat
    * GET /api/rooms/{id} - Lấy thông tin phòng
    * GET /api/rooms/user/{userId} - Lấy danh sách phòng của user
    * PUT /api/rooms/{id} - Cập nhật phòng
    * DELETE /api/rooms/{id} - Xóa phòng
    * POST /api/rooms/{roomId}/members/{userId} - Thêm thành viên
    * DELETE /api/rooms/{roomId}/members/{userId} - Xóa thành viên

## Dependencies
### Frontend
- React + Vite
- Tailwind CSS
- Socket.IO Client

### Backend
- Spring Boot 3.2.0
- Spring Cloud 2023.0.0
- Spring WebSocket
- Spring Data MongoDB
- PostgreSQL
- MongoDB Atlas
- JWT

## Thay Đổi Gần Đây
- Kết nối thành công với MongoDB Atlas
- Triển khai WebSocket và REST endpoints cho messaging service
- Cấu hình giao tiếp giữa các services qua Eureka
- Thêm các API endpoints cho quản lý tin nhắn và phòng chat
- Cập nhật cấu trúc dữ liệu MongoDB cho messages và chat rooms

## Kế Hoạch Tiếp Theo
- Triển khai search service
- Tích hợp file sharing
- Cài đặt monitoring
- Triển khai CI/CD
- Tối ưu hiệu suất MongoDB Atlas
- Thêm caching layer với Redis
