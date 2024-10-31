# Ứng Dụng Chat Realtime

## Giới Thiệu
Ứng dụng chat realtime với giao diện giống Telegram, được xây dựng bằng React và Spring Cloud Microservices.

## Yêu Cầu Hệ Thống
- Java Development Kit (JDK) 17 trở lên
- Node.js 18 trở lên
- PostgreSQL 15 trở lên
- MongoDB 6 trở lên
- RabbitMQ (tùy chọn, cho message broker)

## Cấu Trúc Dự Án
```
chat-app/
├── frontend/            # Ứng dụng React
├── backend/services/    # Microservices
│   ├── service-registry/    # Eureka Server
│   ├── api-gateway/         # API Gateway
│   ├── user-service/        # Quản lý người dùng
│   └── messaging-service/   # Chức năng chat
└── cline_docs/         # Tài liệu dự án
```

## Cài Đặt và Chạy

### 1. Cài Đặt Database
- PostgreSQL:
  ```sql
  CREATE DATABASE chatapp_users;
  ```
- MongoDB:
  ```bash
  use chatapp_messages
  ```

### 2. Backend Services

#### Service Registry (Eureka Server)
```bash
cd backend/services/service-registry/service-registry
mvn spring-boot:run
```
- Truy cập: http://localhost:8761

#### API Gateway
```bash
cd backend/services/api-gateway/api-gateway
mvn spring-boot:run
```
- Cổng: 8080

#### User Service
```bash
cd backend/services/user-service/user-service
mvn spring-boot:run
```
- Cổng: 8081

#### Messaging Service
```bash
cd backend/services/messaging-service/messaging-service
mvn spring-boot:run
```
- Cổng: 8082

### 3. Frontend
```bash
cd frontend
npm install
npm run dev
```
- Truy cập: http://localhost:5173

## API Endpoints

### User Service
- Đăng ký: `POST /api/users`
  ```json
  {
    "username": "example",
    "email": "example@email.com",
    "password": "password123"
  }
  ```
- Lấy thông tin user: `GET /api/users/{id}`
- Cập nhật user: `PUT /api/users/{id}`
- Xóa user: `DELETE /api/users/{id}`

### Messaging Service
- WebSocket endpoint: `ws://localhost:8082/ws`
- Tạo phòng chat: `POST /api/rooms`
  ```json
  {
    "name": "Room Name",
    "isPrivate": false,
    "members": ["userId1", "userId2"]
  }
  ```
- Lấy tin nhắn phòng: `GET /api/messages/room/{roomId}`
- Gửi tin nhắn: Kết nối WebSocket và gửi đến `/app/chat.send`
  ```json
  {
    "content": "Hello!",
    "senderId": "userId",
    "roomId": "roomId"
  }
  ```

## Tính Năng Chính
1. Đăng ký và đăng nhập
2. Chat realtime
3. Tạo phòng chat nhóm
4. Chat riêng tư
5. Gửi file và hình ảnh (đang phát triển)

## Lưu Ý
- Đảm bảo tất cả các services đều đang chạy trước khi sử dụng frontend
- Service Registry phải được khởi động đầu tiên
- Kiểm tra kết nối database trước khi chạy user-service và messaging-service

## Xử Lý Lỗi Thường Gặp
1. Lỗi kết nối database:
   - Kiểm tra thông tin kết nối trong application.yml
   - Đảm bảo database đang chạy

2. Lỗi port đã được sử dụng:
   - Kiểm tra và tắt các ứng dụng đang sử dụng port
   - Có thể thay đổi port trong application.yml

3. Lỗi WebSocket:
   - Kiểm tra CORS configuration
   - Đảm bảo messaging-service đang chạy

## Đóng Góp
Mọi đóng góp đều được chào đón. Vui lòng:
1. Fork dự án
2. Tạo branch mới
3. Commit changes
4. Tạo pull request

## Liên Hệ
- Email: example@email.com
- GitHub: github.com/yourusername