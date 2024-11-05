# Chat Application

A real-time chat application built with Spring Boot microservices and React.

## Features

### Implemented
- User registration and authentication with JWT
- Token refresh mechanism
- Basic authorization
- WebSocket messaging
- Microservices architecture

### Coming Soon
- Email verification
- Password reset
- Real-time chat
- File sharing
- User profiles

## Technology Stack

### Backend
- Java 17
- Spring Boot 3.2.0
- Spring Security with JWT
- Spring WebSocket
- PostgreSQL
- MongoDB
- Netflix Eureka for Service Discovery

### Frontend
- React 18
- Vite
- TypeScript
- Tailwind CSS

### Monitoring
- Grafana
- Prometheus

## Services

1. User Service (Port: 8081)
   - User management
   - Authentication
   - Authorization

2. Messaging Service (Port: 8082)
   - Real-time messaging
   - Message history
   - Chat room management

3. API Gateway (Port: 8080)
   - Request routing
   - Load balancing
   - Security

4. Service Registry (Port: 8761)
   - Service discovery
   - Load balancing

## Getting Started

### Prerequisites
- Java 17
- Node.js 16+
- PostgreSQL
- MongoDB
- Docker (optional)

### Running the Application

1. Start the databases:
```bash
# PostgreSQL should be running on port 5433
# MongoDB should be running on port 27017
```

2. Start the backend services in the following order:
```bash
# 1. Start Service Registry first
cd backend/services/service-registry/service-registry
mvn spring-boot:run

# 2. Start User Service
cd backend/services/user-service/user-service
mvn spring-boot:run

# 3. Start Messaging Service
cd backend/services/messaging-service/messaging-service
mvn spring-boot:run

# 4. Start API Gateway
cd backend/services/api-gateway/api-gateway
mvn spring-boot:run
```

3. Start the frontend:
```bash
cd frontend
npm install
npm run dev
```

## API Documentation

### Authentication Endpoints
```
POST /api/auth/register - Register new user
POST /api/auth/login - User login
POST /api/auth/refresh - Refresh access token
```

### User Endpoints
```
GET /api/users/me - Get current user
GET /api/users/{id} - Get user by ID
GET /api/users/email/{email} - Get user by email
```

## Contributing
Please read CONTRIBUTING.md for details on our code of conduct and the process for submitting pull requests.

## License
This project is licensed under the MIT License - see the LICENSE.md file for details