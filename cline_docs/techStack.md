# Stack Công Nghệ

## Frontend
- **Framework**: React với Vite
- **Styling**: 
  - Tailwind CSS
  - SCSS cho các styles phức tạp
  - CSS Variables cho theme Telegram
  - Framer Motion cho animations

- **UI Components**: 
  - Radix UI/shadcn cho base components
  - Custom components cho Telegram style
  - React Icons
  - Custom SVG icons giống Telegram

- **State Management**: 
  - Zustand cho global state
  - Jotai cho atomic state
  - React Query cho server state

- **Real-time Communication**: 
  - WebSocket (STOMP over SockJS)
  - WebRTC cho voice/video (tương lai)

## Backend
- **API Gateway**: Spring Cloud Gateway
- **Service Registry**: Netflix Eureka Server
- **Microservices**: 
  - Spring Boot 3.2.0
  - Spring Cloud
  - Spring WebSocket cho real-time messaging
  - Spring Data MongoDB
  - Spring Data JPA

- **Authentication**: JWT (planned)

## Cơ Sở Dữ liệu
- **User Service**: 
  - PostgreSQL
  - Database: chatapp_users
  - Port: 5433

- **Messaging Service**:
  - MongoDB Atlas
  - Connection string: mongodb+srv://<username>:<password>@hiep.segu1.mongodb.net/
  - Database: chatapp_messages
  - Collections:
    * messages: Lưu trữ tin nhắn
    * chat_rooms: Quản lý phòng chat

- **Cache**: Redis (planned)

## Service Ports
- Eureka Server: 8761
- API Gateway: 8080
- User Service: 8081
- Messaging Service: 8082

## API Endpoints
### Messaging Service
- **Messages**:
  - POST /api/messages - Tạo tin nhắn mới
  - GET /api/messages/room/{roomId} - Lấy tin nhắn của phòng
  - GET /api/messages/user/{userId} - Lấy tin nhắn của user
  - DELETE /api/messages/{id} - Xóa tin nhắn

- **Chat Rooms**:
  - POST /api/rooms - Tạo phòng chat mới
  - GET /api/rooms/{id} - Lấy thông tin phòng
  - GET /api/rooms/user/{userId} - Lấy danh sách phòng của user
  - PUT /api/rooms/{id} - Cập nhật thông tin phòng
  - DELETE /api/rooms/{id} - Xóa phòng
  - POST /api/rooms/{roomId}/members/{userId} - Thêm thành viên
  - DELETE /api/rooms/{roomId}/members/{userId} - Xóa thành viên

## UI/UX Tools
- **Design System**: 
  - Figma cho UI planning
  - Storybook cho component development

- **Animations**: 
  - Framer Motion
  - GSAP cho animations phức tạp
  - Lottie cho micro-interactions

- **Testing**: 
  - Vitest
  - React Testing Library
  - Cypress cho E2E

## DevOps & Công Cụ
- **Container**: Docker (planned)
- **CI/CD**: GitHub Actions (planned)
- **Monitoring**: Prometheus & Grafana (planned)
- **Testing**: JUnit 5, React Testing Library

## Quyết Định Kiến Trúc
### Frontend
- Sử dụng Vite thay vì Create React App để có hiệu suất phát triển tốt hơn
- Tailwind CSS + SCSS cho styling linh hoạt
- Framer Motion cho animations mượt mà giống Telegram
- Custom components dựa trên Radix UI để có UI/UX giống Telegram
- Xử lý duplicate messages trong useChat hook

### Backend
- Microservices architecture cho khả năng mở rộng tốt
- Service Registry (Eureka) cho service discovery
- WebSocket và REST endpoints cho giao tiếp đa dạng
- JWT cho xác thực bảo mật (planned)
- Xử lý LocalDateTime serialization với Jackson

### Database
- PostgreSQL cho dữ liệu người dùng và xác thực
- MongoDB Atlas cho tin nhắn và dữ liệu real-time:
  * Khả năng mở rộng cao
  * Hỗ trợ tốt cho dữ liệu phi cấu trúc
  * Tích hợp sẵn sharding và replication
- Redis cho caching và tối ưu hiệu suất (planned)

## Xử lý Date/Time
- Sử dụng java.time.LocalDateTime cho backend
- Cấu hình Jackson để hỗ trợ serialization/deserialization của LocalDateTime
- Sử dụng ISO 8601 format cho date/time trong giao tiếp giữa frontend và backend
