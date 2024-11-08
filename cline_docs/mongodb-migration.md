# Phân Tích Chuyển Đổi Database sang MongoDB

## 1. Cấu Trúc Dữ Liệu Hiện Tại

### PostgreSQL (User)
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String email;
    private String password;
    private Role role;
}
```

### MongoDB (Messages)
- Collection: messages
- Collection: chatrooms

## 2. Ưu Điểm Chuyển Sang MongoDB

1. **Đơn Giản Hóa Infrastructure**:
   - Chỉ cần quản lý một database
   - Giảm complexity trong deployment
   - Dễ dàng backup và restore

2. **Hiệu Năng**:
   - Giảm network latency (không cần join giữa các database)
   - Tối ưu cho real-time operations
   - Scaling dễ dàng hơn

3. **Flexibility**:
   - Schema linh hoạt hơn
   - Dễ dàng thêm fields mới
   - Hỗ trợ tốt cho nested data

## 3. Nhược Điểm

1. **ACID Transactions**:
   - MongoDB có hỗ trợ transactions nhưng không mạnh bằng PostgreSQL
   - Cần cẩn thận với các operations liên quan đến tài khoản

2. **Migration Effort**:
   - Cần viết lại các repository classes
   - Thay đổi cấu trúc authentication
   - Cập nhật queries

3. **Performance Concerns**:
   - Cần tối ưu indexes cho user queries
   - Memory usage có thể tăng

## 4. Cấu Trúc MongoDB Mới

### Users Collection
```json
{
  "_id": ObjectId,
  "username": String,
  "email": String,
  "password": String,
  "role": String,
  "createdAt": Date,
  "lastLogin": Date,
  "status": String,
  "profile": {
    "avatar": String,
    "bio": String,
    "settings": Object
  }
}
```

### Messages Collection
```json
{
  "_id": ObjectId,
  "roomId": String,
  "senderId": ObjectId,
  "content": String,
  "timestamp": Date,
  "status": String,
  "readBy": [ObjectId]
}
```

### ChatRooms Collection
```json
{
  "_id": ObjectId,
  "name": String,
  "type": String,
  "participants": [ObjectId],
  "lastMessage": {
    "content": String,
    "senderId": ObjectId,
    "timestamp": Date
  },
  "settings": {
    "notifications": Boolean,
    "theme": String
  }
}
```

## 5. Các Bước Chuyển Đổi

1. **Cập Nhật Dependencies**:
```xml
<!-- Xóa -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>

<!-- Thêm -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

2. **Cập Nhật User Model**:
```java
@Document(collection = "users")
public class User {
    @Id
    private String id;
    @Indexed(unique = true)
    private String username;
    @Indexed(unique = true)
    private String email;
    private String password;
    private Role role;
}
```

3. **Cập Nhật Repository**:
```java
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
```

4. **Cập Nhật Configuration**:
```yaml
spring:
  data:
    mongodb:
      uri: mongodb+srv://username:password@cluster.mongodb.net/chatapp
      auto-index-creation: true
```

## 6. Indexes Cần Thiết

```javascript
// Users Collection
db.users.createIndex({ "username": 1 }, { unique: true })
db.users.createIndex({ "email": 1 }, { unique: true })

// Messages Collection
db.messages.createIndex({ "roomId": 1, "timestamp": -1 })
db.messages.createIndex({ "senderId": 1 })

// ChatRooms Collection
db.chatrooms.createIndex({ "participants": 1 })
db.chatrooms.createIndex({ "lastMessage.timestamp": -1 })
```

## 7. Migration Script

```java
@Component
@Slf4j
public class DatabaseMigrationService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    public void migrateUsers() {
        String sql = "SELECT * FROM users";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> {
            User user = new User();
            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setRole(Role.valueOf(rs.getString("role")));
            return user;
        });
        
        mongoTemplate.insertAll(users);
        log.info("Migrated {} users to MongoDB", users.size());
    }
}
```

## 8. Kết Luận

Việc chuyển sang MongoDB là khả thi và mang lại nhiều lợi ích:
1. Đơn giản hóa infrastructure
2. Tăng tính linh hoạt của dữ liệu
3. Phù hợp với real-time chat application

Tuy nhiên cần lưu ý:
1. Cẩn thận trong migration process
2. Đảm bảo indexes được tạo đúng
3. Test kỹ các authentication flows
4. Monitor performance sau migration
