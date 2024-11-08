# Codebase Summary

## Key Components & Interactions

### Database Structure

#### MongoDB Collections
1. users
   ```json
   {
     "_id": ObjectId,
     "username": String,
     "email": String,
     "password": String,
     "role": "ADMIN" | "USER",
     "createdAt": Date,
     "lastLogin": Date,
     "status": "ONLINE" | "OFFLINE" | "AWAY",
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

2. chat_rooms
   ```json
   {
     "_id": ObjectId,
     "name": String,
     "members": [String],
     "createdAt": Date,
     "updatedAt": Date
   }
   ```

3. messages
   ```json
   {
     "_id": ObjectId,
     "content": String,
     "senderId": String,
     "roomId": String,
     "createdAt": Date
   }
   ```

### Backend Services

#### 1. API Gateway
- **Vị trí**: `/backend/services/api-gateway`
- **Chức năng chính**:
  - Route management
  - Authentication filter
  - CORS configuration
  - Load balancing
- **Tương tác**:
  - Điểm vào cho tất cả client requests
  - Giao tiếp với Service Registry
  - Forward requests tới các services

#### 2. User Service
- **Vị trí**: `/backend/services/user-service`
- **Chức năng chính**:
  - User authentication & authorization
  - Profile management
  - JWT handling
  - Friend system management
- **Components**:
  - AuthController: Xử lý đăng nhập/đăng ký
  - UserController: Quản lý user profile và friends
  - Security filters: JWT validation
  - Custom UserDetailsService
- **Key Features**:
  - JWT-based authentication
  - Profile CRUD operations
  - Friend management system
  - User search functionality

#### 3. Message Service
- **Vị trí**: `/backend/services/messaging-service`
- **Chức năng chính**:
  - Real-time messaging
  - Chat room management
  - WebSocket handling
- **Components**:
  - ChatWebSocketController
  - MessageController
  - ChatRoomController
  - WebSocket configuration

### Frontend Structure

#### 1. Authentication Components
- **Vị trí**: `/frontend/src/components/auth`
- **Key Files**:
  - LoginForm.tsx: Form đăng nhập với validation
  - RegisterForm.tsx: Form đăng ký
- **Features**:
  - Form validation
  - Error handling
  - Loading states
  - Redirect logic

#### 2. Authentication Context
- **Vị trí**: `/frontend/src/contexts`
- **Key Files**:
  - AuthContext.tsx: Global auth state management
- **Features**:
  - Token management
  - Auto refresh (14 phút interval)
  - User data persistence
  - Logout handling

#### 3. Core Components
- **Vị trí**: `/frontend/src/components`
- **Chức năng**:
  - Authentication UI
  - Chat interface
  - User status indicators
  - Room management UI

#### 4. State Management
- **Vị trí**: `/frontend/src/store`
- **Implementation**:
  - Redux store configuration
  - Auth slice
  - Chat state management

#### 5. Services & Utils
- **Vị trí**: `/frontend/src/services`
- **Chức năng**:
  - API integration
  - WebSocket services
  - Room management services

## Recent Changes

### Database Migration
- Chuyển đổi toàn bộ sang MongoDB
- Cập nhật cấu trúc User model
- Thêm UserStatus enum
- Chuẩn hóa email domain (@chatapp.com)

### Authentication Enhancement
- Cải thiện JWT handling
- Thêm refresh token support
- Tăng cường security filters
- Thêm friend system

### Real-time Features
- Typing indicators
- User status tracking
- Room management
- WebSocket reliability

### Infrastructure Updates
- Setup monitoring với Prometheus & Grafana
- Cấu hình logging với ELK Stack
- Cải thiện service discovery
- Tăng cường error handling

## Pending Improvements
1. Notification System Integration
   - Push notifications
   - Email notifications
   - Notification preferences

2. Search Functionality
   - User search optimization
   - Message search
   - Elasticsearch integration

3. Video Call Features
   - WebRTC setup
   - Call management
   - Screen sharing

4. Enhanced Monitoring
   - Detailed metrics
   - Performance tracking
   - Error monitoring