# Database Migration Guide

## Thay đổi cấu trúc

### Messages Collection
1. Đổi tên trường:
   - `timestamp` -> `createdAt`

2. Thêm trường mới:
   - `status`: MessageStatus enum (SENT, DELIVERED, READ)

3. Xóa trường không cần thiết:
   - `type` (mặc định là "text")
   - `receiverId` (đã có roomId)

### ChatRooms Collection
1. Thêm trường mới:
   - `lastMessage`: Message object
   - `settings`: RoomSettings object
     + `mutedUsers`: List<String>
     + `blockedUsers`: List<String>
     + `admins`: List<String>
     + `muteNotifications`: boolean

2. Xóa trường không cần thiết:
   - `unreadCount` (có thể tính từ lastMessage)

## Cách chạy migration

Migration sẽ tự động chạy khi service khởi động nhờ annotation `@PostConstruct`.

Các bước thực hiện:
1. Đảm bảo service đã dừng
2. Backup database hiện tại
3. Khởi động lại service để chạy migration
4. Kiểm tra logs để xác nhận migration thành công

## Kiểm tra sau migration

1. Kiểm tra Messages:
```javascript
db.messages.findOne()
// Kết quả mong đợi:
{
    "_id": ObjectId("..."),
    "content": "...",
    "senderId": "...",
    "roomId": "...",
    "createdAt": ISODate("..."),
    "status": "SENT",
    "_class": "com.chatapp.model.Message"
}
```

2. Kiểm tra ChatRooms:
```javascript
db.chat_rooms.findOne()
// Kết quả mong đợi:
{
    "_id": ObjectId("..."),
    "name": "...",
    "members": ["..."],
    "lastMessage": {
        "content": "...",
        "senderId": "...",
        "createdAt": ISODate("...")
    },
    "settings": {
        "mutedUsers": [],
        "blockedUsers": [],
        "admins": ["..."],
        "muteNotifications": false
    },
    "createdAt": ISODate("..."),
    "updatedAt": ISODate("..."),
    "_class": "com.chatapp.model.ChatRoom"
}
```

## Rollback

Nếu cần rollback:
1. Dừng service
2. Restore database từ backup
3. Khởi động lại service

## Lưu ý

- Migration này là một-chiều, không thể tự động rollback
- Đảm bảo đã backup database trước khi chạy
- Kiểm tra kỹ logs trong quá trình migration
- Nếu có lỗi, restore từ backup và kiểm tra lại code