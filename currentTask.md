# Các nhiệm vụ hiện tại

## Vấn đề đang gặp phải
- JWT Authentication không hoạt động giữa các service
- Đã đơn giản hóa cấu trúc bảo mật:
  - Đã xóa JWT filter ở messaging service
  - Chuyển toàn bộ xác thực về API Gateway
  - Cập nhật cấu hình CORS và security

## Các nhiệm vụ Backend ưu tiên

1. Service Registry (Eureka Server)
- [x] Tạo cấu hình Eureka Server cơ bản
- [x] Cấu hình service discovery cho tất cả microservices
- [x] Thêm health checks và monitoring
- [x] Cấu hình logging

2. API Gateway
- [x] Cấu hình routes cho tất cả services
- [x] Cài đặt CORS configuration
- [x] Thêm authentication filter
- [x] Cấu hình load balancing
- [ ] Fix lỗi JWT validation

3. Messaging Service
- [x] Cấu trúc cơ bản
- [x] Models và repositories
- [x] Cấu hình WebSocket
- [x] Quản lý phòng chat
- [x] Lưu trữ tin nhắn
- [x] Thông báo real-time
- [x] Chỉ báo đang nhập
- [x] Cập nhật trạng thái người dùng

4. User Service
- [x] Xác thực cơ bản
- [x] Đăng ký người dùng
- [x] Triển khai JWT
- [x] Quản lý hồ sơ người dùng
- [x] Hệ thống bạn bè/liên hệ
- [x] Chức năng tìm kiếm người dùng

5. Video Call Service (Mới)
- [ ] Cài đặt WebRTC
- [ ] Máy chủ báo hiệu
- [ ] Cấu hình TURN/STUN
- [ ] Hỗ trợ gọi nhóm
- [ ] Chia sẻ màn hình

6. Notification Service (Mới)
- [ ] Thông báo real-time
- [ ] Push notifications
- [ ] Thông báo qua email
- [ ] Tùy chọn thông báo

## Nhiệm vụ hạ tầng

1. Cài đặt Database
- [x] PostgreSQL cho User Service
- [x] MongoDB cho Message Service
- [x] Redis cho caching
- [ ] Elasticsearch cho tìm kiếm

2. Giám sát
- [ ] Cấu hình Prometheus
- [ ] Thiết lập Grafana dashboards
- [ ] Triển khai logging với ELK Stack

3. Hạ tầng tìm kiếm
- [ ] Cài đặt Elasticsearch
- [ ] Cấu hình Logstash pipeline
- [ ] Thiết lập Kibana dashboards
- [ ] Triển khai các API tìm kiếm

## Các bước tiếp theo ưu tiên:
1. Fix lỗi JWT authentication
2. Kiểm tra lại luồng xác thực
3. Triển khai hệ thống thông báo