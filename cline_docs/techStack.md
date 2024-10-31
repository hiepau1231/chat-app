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
  - Socket.IO Client
  - WebRTC cho voice/video (tương lai)

## Backend
- **API Gateway**: Spring Cloud Gateway
- **Microservices**: Spring Cloud
- **Real-time Server**: Socket.IO
- **Authentication**: JWT

## Cơ Sở Dữ Liệu
- **Chính**: PostgreSQL
- **Cache**: Redis
- **Real-time Data**: MongoDB

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
- **Container**: Docker
- **CI/CD**: GitHub Actions
- **Monitoring**: Prometheus & Grafana
- **Testing**: Jest, React Testing Library

## Quyết Định Kiến Trúc
### Frontend
- Sử dụng Vite thay vì Create React App để có hiệu suất phát triển tốt hơn
- Tailwind CSS + SCSS cho styling linh hoạt
- Framer Motion cho animations mượt mà giống Telegram
- Custom components dựa trên Radix UI để có UI/UX giống Telegram

### Backend
- Microservices architecture cho khả năng mở rộng tốt
- WebSocket cho giao tiếp thời gian thực
- JWT cho xác thực bảo mật

### Database
- PostgreSQL cho dữ liệu người dùng và xác thực
- MongoDB cho tin nhắn và dữ liệu real-time
- Redis cho caching và tối ưu hiệu suất