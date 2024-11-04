# Project Overview
Hệ thống chat thời gian thực với kiến trúc microservices, tập trung vào khả năng mở rộng và hiệu suất cao.

# Core Functionalities

1. Service Infrastructure Setup
   1. ✅ Setup Service Registry (Eureka)
      1. ✅ Configure Eureka Server properties
      2. ✅ Enable service registration and discovery
      3. ✅ Setup health check endpoints
   2. ✅ Configure API Gateway
      1. ✅ Setup routing rules for all services
      2. ✅ Implement rate limiting (Redis rate limiter)
      3. ✅ Configure load balancing (lb:// routing)
   3. ⚠️ Setup Monitoring
      1. ✅ Implement logging system (ELK Stack)
      2. ✅ Configure metrics collection (Prometheus)
      3. ⚠️ Setup monitoring dashboards (Grafana pending)

2. Database Implementation
   1. ✅ Setup MongoDB for Messages & Rooms
      1. ✅ Design message collection schema
      2. ✅ Design chat room collection schema
      3. ✅ Implement indexes for efficient queries
   2. ✅ Setup PostgreSQL for User Data
      1. ✅ Design user table schema
      2. ✅ Setup authentication tables
      3. �� Configure connection pools
   3. ❌ Setup Redis for Caching
      1. ❌ Configure cache settings
      2. ❌ Implement cache strategies
      3. ❌ Setup cache invalidation

3. Backend Services Development
   1. User Service Implementation
      1. ✅ Create user management APIs
         1. ✅ Registration endpoint
         2. ✅ Login endpoint
         3. ❌ Profile management endpoints
      2. ⚠️ Implement authentication
         1. ✅ JWT token generation
         2. ✅ Password encryption
         3. ❌ Session management
         4. ❌ Refresh token support
      3. ❌ User presence tracking
         1. ❌ Online status management
         2. ❌ Last seen updates
         3. ❌ Activity logging
   
   2. Messaging Service Implementation
      1. ✅ Create message handling system
         1. ✅ Message creation endpoints
         2. ✅ Message retrieval endpoints
         3. ⚠️ Message update/delete endpoints
            1. ✅ Delete single message
            2. ❌ Delete entire chat room
            3. ❌ Message forwarding API
      2. ⚠️ Implement WebSocket
         1. ✅ Configure WebSocket server
         2. ✅ Setup message broadcasting
         3. ⚠️ Implement typing indicators
            1. ✅ Send typing status
            2. ✅ Broadcast typing events
            3. ❌ Handle typing timeout
      3. ⚠️ Chat room management
         1. ✅ Room creation/deletion
         2. ✅ Member management
         3. ❌ Room settings management
            1. ❌ Mute notifications
            2. ❌ Block users
            3. ❌ Admin privileges

   3. ⚠️ Message Enhancement Features
      1. ⚠️ Unread Messages System
         1. ✅ Track last read message
         2. ✅ Count unread messages
         3. ❌ Mark messages as read
      2. ❌ Message Actions
         1. ❌ Copy message
         2. ❌ Forward message
         3. ❌ Delete message
      3. ❌ Emoji Support
         1. ❌ Emoji picker
         2. ❌ Emoji reactions
         3. ❌ Emoji in messages

4. Frontend Development
   1. ✅ Setup React Application
      1. ✅ Configure project structure
      2. ✅ Setup routing system (react-router-dom)
      3. ✅ Implement state management (Redux Toolkit)
   
   2. Core Components Development
      1. ⚠️ Authentication components
         1. ✅ Login form (UI + Redux)
         2. ✅ Registration form (UI + Redux)
         3. ❌ Profile settings
      2. ⚠️ Chat interface
         1. ✅ Message list component (basic)
         2. ✅ Input area component (basic)
         3. ⚠️ Chat room list
      3. ⚠️ Message Enhancement UI
         1. ✅ Typing indicator display
         2. ✅ Unread message counter
         3. ❌ Last read indicator
      4. ❌ Message Action Components
         1. ❌ Message context menu
         2. ❌ Forward message modal
         3. ❌ Delete confirmation
      5. ❌ User Management UI
         1. ❌ User block interface
         2. ❌ Mute notifications toggle
         3. ❌ Admin controls
      6. ❌ Media Components
         1. ❌ File upload interface
         2. ❌ Media preview
         3. ❌ Emoji picker

   3. Real-time Features Integration
      1. ⚠️ WebSocket client setup
         1. ✅ Connection management
         2. ✅ Message handling
         3. ❌ Status updates

5. Advanced Features Implementation
   1. ❌ Voice/Video Call Service
      1. ❌ WebRTC Server Setup
         1. ❌ Signaling server implementation
         2. ❌ ICE server configuration
         3. ❌ Room management for calls
      2. ❌ Call Features
         1. ❌ One-to-one calls
         2. ❌ Group calls
         3. ❌ Screen sharing
      3. ❌ Call Quality Management
         1. ❌ Bandwidth adaptation
         2. ❌ Connection monitoring
         3. ❌ Fallback mechanisms

   2. ❌ Search Service Implementation
      1. ❌ Elasticsearch Setup
         1. ❌ Configure Elasticsearch cluster
         2. ❌ Define message index mapping
         3. ❌ Setup replication and sharding
      2. ❌ Search Features
         1. ❌ Message content search
         2. ❌ User search
         3. ❌ File/media search
      3. ❌ Search Optimization
         1. ❌ Implement search suggestions
         2. ❌ Setup relevance tuning
         3. ❌ Configure search analytics

   3. ❌ Message Features
      1. ❌ Message Threading
      2. ❌ Reactions System
      3. ❌ Message Translation

6. Testing & Deployment
   1. ❌ Unit Testing
   2. ❌ Performance Testing
   3. ✅ Deployment
      1. ✅ Docker containerization
      2. ❌ Kubernetes orchestration
      3. ❌ CI/CD pipeline setup

# User Interaction Flows

1. Authentication Flow
   1. User Registration Process
      1. User accesses registration page
      2. System displays registration form
      3. User enters: email, username, password
      4. System validates input:
         1. Email format check
         2. Username availability check
         3. Password strength verification
      5. On validation success:
         1. System encrypts password
         2. Creates user record
         3. Sends verification email
      6. User verifies email:
         1. Clicks verification link
         2. System activates account
         3. Redirects to login page

   2. User Login Process
      1. User enters credentials
      2. System validates:
         1. Checks user existence
         2. Verifies password
         3. Checks account status
      3. On successful login:
         1. Generates JWT token
         2. Updates last login timestamp
         3. Sets user status to online
      4. System loads:
         1. User profile data
         2. Chat history
         3. Contact list

2. Chat Interaction Flow
   1. Starting New Chat
      1. User clicks "New Chat":
         1. System shows contact list
         2. User selects contact(s)
         3. For group chat:
            1. User sets group name
            2. Adds multiple contacts
            3. Sets group settings
      2. System creates chat room:
         1. Generates room ID
         2. Adds selected members
         3. Initializes chat history
      3. WebSocket connections:
         1. Creates room subscription
         2. Notifies all members
         3. Updates UI for all participants

   2. Message Exchange Process
      1. User Types Message:
         1. System shows typing indicator
         2. Broadcasts typing status
         3. Other users see indicator
      2. User Sends Message:
         1. Client validates message
         2. Sends to WebSocket server
         3. Server processes message:
            1. Validates content
            2. Stores in database
            3. Broadcasts to room members
      3. Message Receipt:
         1. Recipients' clients receive message
         2. Update chat UI
         3. Send delivery confirmation
      4. Read Receipt Process:
         1. User views message
         2. Client sends read status
         3. Server updates message status
         4. Notifies sender

3. Media Sharing Flow
   1. File Upload Process
      1. User selects file:
         1. Client checks file type
         2. Validates size limits
         3. Shows preview if possible
      2. Upload begins:
         1. Client chunks large files
         2. Shows progress indicator
         3. Handles pause/resume
      3. Server processing:
         1. Validates file
         2. Processes media type:
            1. Compresses images
            2. Generates thumbnails
            3. Transcodes video
         3. Stores in CDN
      4. Completion:
         1. Server sends file URL
         2. Client displays media
         3. Notifies chat members

4. Voice/Video Call Flow
   1. Call Initiation
      1. Caller actions:
         1. Selects contact
         2. Chooses call type
         3. System checks user availability
      2. Connection setup:
         1. Creates WebRTC offer
         2. Establishes signaling
         3. Exchanges ICE candidates
      3. Recipient side:
         1. Receives call notification
         2. Shows incoming call UI
         3. Plays ringtone
   
   2. Active Call Management
      1. Call accepted:
         1. Establishes peer connection
         2. Starts media streams
         3. Updates UI for both parties
      2. During call:
         1. Monitors connection quality
         2. Handles media controls
         3. Supports features:
            1. Mute/unmute
            2. Camera toggle
            3. Screen sharing
      3. Call termination:
         1. Closes media streams
         2. Ends WebRTC connection
         3. Updates call history

5. Notification System Flow
   1. Event Generation
      1. System events:
         1. New message arrival
         2. Mention notifications
         3. Missed calls
      2. Processing:
         1. Creates notification object
         2. Determines priority
         3. Selects delivery method
   
   2. Notification Delivery
      1. Online users:
         1. Real-time WebSocket delivery
         2. UI updates
         3. Sound alerts
      2. Offline users:
         1. Stores notifications
         2. Sends push notification
         3. Queues for delivery
      3. User interaction:
         1. Notification click handling
         2. Mark as read
         3. Clear notifications

# Technical Stack
1. Backend
   - Spring Boot 3.2.0
   - Spring Cloud (Eureka, Gateway)
   - WebSocket (STOMP)
   - MongoDB, PostgreSQL, Redis
   - JWT Authentication

2. Frontend
   - React + TypeScript
   - Redux for state management
   - Socket.io client
   - WebRTC for calls
   - Material UI components

3. Infrastructure
   - Docker & Kubernetes
   - AWS/GCP for hosting
   - GitHub Actions for CI/CD
   - ELK Stack for logging

4. Search Infrastructure
   - Elasticsearch 8.x
   - Logstash for data pipeline
   - Kibana for search analytics

5. Video Call Infrastructure
   - WebRTC
   - TURN/STUN servers
   - Media server for group calls