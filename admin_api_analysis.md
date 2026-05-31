# 📡 API Documentation — ADMIN Role (40 Endpoints)

> **Base URL:** `http://localhost:8080`  
> **Prefix:** `/api/admin/**`  
> **Xác thực:** 🔑 Bắt buộc (Yêu cầu Token JWT Bearer của tài khoản role `ADMIN`)  
> **Chế độ dữ liệu:** `application/json` hoặc `multipart/form-data` (đối với các API hỗ trợ tải ảnh trực tiếp)

---

## 🔑 ĐĂNG NHẬP & LẤY TOKEN (PUBLIC API)
*Trước khi thực hiện các yêu cầu của ADMIN, bạn cần lấy token JWT thông qua API Đăng nhập.*

| Thuộc tính | Giá trị |
|---|---|
| **Method** | `POST` |
| **Endpoint** | `/api/auth/login` |
| **Đầu vào** | Request Body JSON |
| **Mô tả** | Đăng nhập tài khoản quản trị để nhận chuỗi mã JWT Bearer |

**Mẫu Request Body:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Mẫu Response (`200 OK`):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc0NjIwMDAwMCwiZXhwIjoxNzQ2MjA3NjAwfQ...",
  "tokenType": "Bearer",
  "username": "admin",
  "role": "ADMIN"
}
```

---

## I. 📅 QUẢN LÝ LỊCH HẸN & VÒNG ĐỜI ĐƠN ĐẶT (BOOKINGS)

### 1. Xem danh sách lịch hẹn (Phân trang + Lọc)
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/admin/bookings` |
| **Đầu vào** | Query Params: `page` *(mặc định 0)*, `size` *(mặc định 10)*, `status` *(tùy chọn)* |
| **Mô tả** | Trả về danh sách phân trang các đơn đặt lịch hẹn của studio, lọc theo trạng thái nếu được yêu cầu |

**Các trạng thái `status` hợp lệ:** `PENDING` `CONFIRMED` `ASSIGNED` `COMPLETED` `CANCELLED`

**Mẫu Request:**
```
GET /api/admin/bookings?page=0&size=2&status=PENDING
```

**Mẫu Response (`200 OK`):**
```json
[
  {
    "id": 1,
    "bookingCode": "STB-20260531-K7B3A",
    "customerName": "Nguyễn Hoàng Anh",
    "customerEmail": "hoanganh@gmail.com",
    "customerPhone": "0905111222",
    "customerFacebook": "https://facebook.com/hoanganh",
    "shootDate": "2026-06-15",
    "shootTimeSlot": "09:00:00",
    "shootLocation": "Studio - 123 Nguyễn Văn Linh",
    "customerNotes": "Chụp concept trắng ngọt ngào",
    "totalAmount": 2699000.00,
    "bookingStatus": "PENDING",
    "paymentStatus": "UNPAID",
    "createdAt": "2026-05-30T10:00:00",
    "packageName": "GÓI CHỤP BASIC I",
    "conceptTitle": "SWEET ANGEL",
    "assignedStaff": []
  }
]
```

---

### 2. Xem chi tiết một đơn đặt lịch
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/admin/bookings/{id}` |
| **Đầu vào** | Path Variable `id` |
| **Mô tả** | Xem thông tin chi tiết đầy đủ của đơn đặt, bao gồm các nhân sự được phân công |

**Mẫu Request:**
```
GET /api/admin/bookings/1
```

**Mẫu Response (`200 OK`):**
```json
{
  "id": 1,
  "bookingCode": "STB-20260531-K7B3A",
  "customerName": "Nguyễn Hoàng Anh",
  "customerEmail": "hoanganh@gmail.com",
  "customerPhone": "0905111222",
  "shootDate": "2026-06-15",
  "shootTimeSlot": "09:00:00",
  "shootLocation": "Studio - 123 Nguyễn Văn Linh",
  "totalAmount": 2699000.00,
  "bookingStatus": "ASSIGNED",
  "paymentStatus": "DEPOSITED",
  "createdAt": "2026-05-30T10:00:00",
  "packageName": "GÓI CHỤP BASIC I",
  "conceptTitle": "SWEET ANGEL",
  "assignedStaff": [
    { "staffId": 2, "fullName": "Huỳnh Ly Na", "role": "PHOTOGRAPHER" },
    { "staffId": 5, "fullName": "Ly Ly", "role": "MAKEUP" }
  ]
}
```

---

### 3. Cập nhật trạng thái đơn đặt lịch
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `PUT` |
| **Endpoint** | `/api/admin/bookings/{id}/status` |
| **Đầu vào** | Query Params: `status` *(bắt buộc)*, `note` *(tùy chọn)* |
| **Mô tả** | Thay đổi trạng thái tiến độ đơn đặt lịch của khách hàng, tự động ghi nhật ký lịch sử trạng thái và **tự động gửi email thông báo trạng thái mới** tới khách hàng |

**Mẫu Request:**
```
PUT /api/admin/bookings/1/status?status=CONFIRMED&note=Khách đã liên hệ qua điện thoại xác nhận lại
```

**Mẫu Response (`200 OK`):**
```json
{
  "id": 1,
  "bookingCode": "STB-20260531-K7B3A",
  "bookingStatus": "CONFIRMED",
  "paymentStatus": "UNPAID",
  "message": "Cập nhật trạng thái đơn chụp và gửi email thành công!"
}
```

---

### 4. Cập nhật trạng thái thanh toán
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `PUT` |
| **Endpoint** | `/api/admin/bookings/{id}/payment` |
| **Đầu vào** | Query Params: `status` *(bắt buộc)*, `method` *(mặc định BANK)* |
| **Mô tả** | Cập nhật tiến độ thanh toán của đơn. Nếu trạng thái là `DEPOSITED` hoặc `PAID` và đơn đang ở trạng thái `PENDING`, hệ thống tự động chuyển trạng thái đơn sang `CONFIRMED`, ghi audit logs và **tự động gửi email xác nhận giao dịch** tới khách hàng |

**Các trạng thái `status` hợp lệ:** `UNPAID` `DEPOSITED` `PAID` `REFUNDED`

**Mẫu Request:**
```
PUT /api/admin/bookings/1/payment?status=DEPOSITED&method=BANK
```

**Mẫu Response (`200 OK`):**
```json
{
  "id": 1,
  "bookingCode": "STB-20260531-K7B3A",
  "bookingStatus": "CONFIRMED",
  "paymentStatus": "DEPOSITED"
}
```

---

### 5. Phân công nhân sự (Photographer & Makeup)
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `POST` |
| **Endpoint** | `/api/admin/bookings/{id}/assign` |
| **Đầu vào** | Query Params: `photographerId` *(bắt buộc)*, `makeupId` *(bắt buộc)* |
| **Mô tả** | Phân công thợ chụp ảnh và thợ trang điểm cho đơn đặt lịch. Tự động kiểm tra kẹt ca chụp đối với cả 2 nhân viên để tránh trùng lịch, chuyển đơn chụp sang `ASSIGNED` và **gửi email thông báo ê kíp phục vụ** tới khách hàng |

**Mẫu Request:**
```
POST /api/admin/bookings/1/assign?photographerId=2&makeupId=5
```

**Mẫu Response (`200 OK`):**
```json
{
  "id": 1,
  "bookingCode": "STB-20260531-K7B3A",
  "bookingStatus": "ASSIGNED",
  "assignedStaff": [
    { "staffId": 2, "fullName": "Huỳnh Ly Na", "role": "PHOTOGRAPHER" },
    { "staffId": 5, "fullName": "Ly Ly", "role": "MAKEUP" }
  ]
}
```

**Lỗi kẹt lịch (`400 Bad Request`):**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Không thể phân công: Thợ chụp ảnh Huỳnh Ly Na đã kẹt ca làm việc khác vào cùng thời điểm này."
}
```

---

### 6. Xem lịch sử thay đổi trạng thái đơn
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/admin/bookings/{id}/history` |
| **Đầu vào** | Path Variable `id` |
| **Mô tả** | Lấy danh sách nhật ký thay đổi trạng thái của lịch hẹn: ai thay đổi, thời gian, trạng thái cũ, trạng thái mới và lý do ghi chú. **Được bảo vệ tối đa khỏi lỗi serialization nhờ chú thích thực thể thông minh.** |

**Mẫu Request:**
```
GET /api/admin/bookings/1/history
```

**Mẫu Response (`200 OK`):**
```json
[
  {
    "id": 12,
    "previousStatus": "PENDING",
    "newStatus": "CONFIRMED",
    "note": "Hệ thống tự động Xác nhận (CONFIRMED) sau khi ghi nhận giao dịch tài chính: DEPOSITED",
    "changedAt": "2026-05-30T10:15:30",
    "changedBy": {
      "id": 1,
      "username": "admin",
      "fullName": "Quản trị viên hệ thống"
    }
  }
]
```

---

## II. 🎬 GIÁM SÁT HẬU KỲ & BÀN GIAO SẢN PHẨM (POST-PRODUCTION)

### 7. Theo dõi danh sách hậu kỳ toàn studio (có phân trang)
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/admin/post-productions` |
| **Đầu vào** | - Query Param `status` *(tùy chọn)*<br>- Query Param `page` *(mặc định 0)*<br>- Query Param `size` *(mặc định 10)* |
| **Mô tả** | Lấy toàn bộ tiến độ sản xuất, chỉnh sửa ảnh. Ngăn ngừa triệt để lỗi serialization nhờ nạp trước dữ liệu Eager |

**Các trạng thái `status` hợp lệ:** `UNPROCESSED` `EDITING` `WAITING_APPROVAL` `DELIVERED`

**Mẫu Request:**
```
GET /api/admin/post-productions?status=EDITING
```

**Mẫu Response (`200 OK`):**
```json
[
  {
    "id": 4,
    "booking": {
      "id": 1,
      "bookingCode": "STB-20260531-K7B3A",
      "customerName": "Nguyễn Hoàng Anh"
    },
    "productionStatus": "EDITING",
    "rawPhotoLink": "https://drive.google.com/drive/folders/raw123",
    "editedPhotoLink": null,
    "note": "Đang lên tông màu trắng lông vũ.",
    "updatedBy": {
      "id": 1,
      "username": "admin",
      "fullName": "Quản trị viên"
    },
    "updatedAt": "2026-05-31T01:00:00"
  }
]
```

---

### 8. Cập nhật tiến độ hậu kỳ & Bàn giao link ảnh
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `PUT` |
| **Endpoint** | `/api/admin/bookings/{bookingId}/post-production` |
| **Đầu vào** | Request Body JSON |
| **Mô tả** | Ghi nhận link ảnh thô, link ảnh đã chỉnh sửa hoàn chỉnh và cập nhật tiến độ sản xuất ảnh |

**Mẫu Request Body:**
```json
{
  "productionStatus": "DELIVERED",
  "rawPhotoLink": "https://drive.google.com/drive/folders/raw123",
  "editedPhotoLink": "https://drive.google.com/drive/folders/final123",
  "note": "Ảnh chỉnh sửa màu Sweet Angel hoàn tất theo yêu cầu."
}
```

**Mẫu Response (`200 OK`):**
```json
{
  "id": 4,
  "productionStatus": "DELIVERED",
  "rawPhotoLink": "https://drive.google.com/drive/folders/raw123",
  "editedPhotoLink": "https://drive.google.com/drive/folders/final123",
  "note": "Ảnh chỉnh sửa màu Sweet Angel hoàn tất theo yêu cầu.",
  "updatedAt": "2026-05-31T01:10:00"
}
```

---

## III. 📊 BÁO CÁO & THỐNG KÊ DOANH THU (DASHBOARD)

### 9. Thống kê báo cáo doanh thu thực tế
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/admin/dashboard/revenue` |
| **Đầu vào** | Query Params: `startDate`, `endDate` *(yyyy-MM-dd, bắt buộc)* |
| **Mô tả** | Tính tổng doanh thu thực tế và thống kê doanh thu theo ngày từ các đơn chụp đã đặt cọc/thanh toán xong. Thống kê mức độ được ưa chuộng của các gói chụp |

**Mẫu Request:**
```
GET /api/admin/dashboard/revenue?startDate=2026-05-01&endDate=2026-05-31
```

**Mẫu Response (`200 OK`):**
```json
{
  "totalRevenue": 7198000.00,
  "totalBookings": 2,
  "revenueByDate": [
    { "date": "2026-05-15", "amount": 2699000.00 },
    { "date": "2026-05-20", "amount": 4499000.00 }
  ],
  "packagePopularity": [
    { "packageName": "GÓI CHỤP LUXURY", "bookingCount": 1 },
    { "packageName": "GÓI CHỤP BASIC I", "bookingCount": 1 }
  ]
}
```

---

### 10. Xem thống kê Dashboard nhanh
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/admin/dashboard/statistics` |
| **Đầu vào** | Không có |
| **Mô tả** | Lấy nhanh các con số tổng quan: tổng nhân viên, tổng số đơn đặt, số đơn chờ duyệt, số gói dịch vụ đang hoạt động |

**Mẫu Request:**
```
GET /api/admin/dashboard/statistics
```

**Mẫu Response (`200 OK`):**
```json
{
  "totalStaff": 6,
  "totalBookings": 45,
  "pendingBookings": 3,
  "activePackages": 5
}
```

---

## IV. 👥 QUẢN LÝ NHÂN SỰ & KHÁCH HÀNG (STAFF & CUSTOMERS)

### 11. Xem danh sách toàn bộ nhân sự (có phân trang)
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/admin/staff` |
| **Đầu vào** | - Query Param `page` *(mặc định 0)*<br>- Query Param `size` *(mặc định 10)* |
| **Mô tả** | Trả về thông tin đầy đủ và hồ sơ năng lực của tất cả nhân sự đang đăng ký tại hệ thống |

**Mẫu Response (`200 OK`):**
```json
[
  {
    "profileId": 1,
    "userId": 2,
    "fullName": "Huỳnh Ly Na",
    "roleName": "PHOTOGRAPHER",
    "avatarUrl": "https://res.cloudinary.com/.../avatar.jpg",
    "bio": "Đam mê nhiếp ảnh.",
    "yearsOfExperience": 5
  }
]
```

---

### 12. Tạo tài khoản nhân viên mới
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `POST` |
| **Endpoint** | `/api/admin/staff` |
| **Đầu vào** | **JSON Mode**: `@RequestBody` JSON  <br> **Multipart Mode**: `@RequestPart("staff")` DTO JSON + `@RequestPart("avatarFile")` File ảnh nhị phân |
| **Mô tả** | Tạo tài khoản đăng nhập cho nhân viên đồng thời tạo hồ sơ năng lực. **Nếu gửi file, backend tự up lên Cloudinary.** |

*   **Chế độ JSON (`Content-Type: application/json`):**
```json
{
  "username": "photographer.minh",
  "password": "SecretPassword123",
  "fullName": "Nguyễn Hoàng Minh",
  "email": "minh.photo@gmail.com",
  "phone": "0987654321",
  "roleId": 3,
  "avatarUrl": "https://res.cloudinary.com/.../minh.jpg",
  "yearsOfExperience": 5
}
```

*   **Chế độ Multipart (`Content-Type: multipart/form-data`):**
    *   Part `staff` (JSON, `application/json`): bỏ trường `avatarUrl` trong JSON.
    *   Part `avatarFile` (File nhị phân): Chọn hình ảnh từ máy tính.

---

### 13. Cập nhật hồ sơ nhân viên
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `PUT` |
| **Endpoint** | `/api/admin/staff/{id}` |
| **Đầu vào** | Path `id` + **JSON Mode** / **Multipart Mode** |
| **Mô tả** | Cập nhật hồ sơ năng lực, ảnh đại diện, số năm kinh nghiệm và mạng xã hội của nhân viên. |

**Chế độ Multipart (`Content-Type: multipart/form-data`):**
*   Part `profile` (JSON, `application/json`): chứa thông tin cập nhật.
*   Part `avatarFile` (File nhị phân, tùy chọn): ảnh đại diện mới để tự up lên Cloudinary.

---

### 14. Bật/Tắt quyền hoạt động đăng nhập
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `PUT` |
| **Endpoint** | `/api/admin/staff/{id}/toggle-active` |
| **Đầu vào** | Path Variable `id` *(Profile ID hoặc User ID)* |
| **Mô tả** | Khóa hoặc kích hoạt lại quyền đăng nhập vào hệ thống của nhân viên đó |

**Mẫu Request:**
```
PUT /api/admin/staff/1/toggle-active
```

**Mẫu Response (`200 OK`):**
```json
{
  "message": "Thay đổi trạng thái hoạt động tài khoản thành công!"
}
```

---

### 15. Bật/Tắt hiển thị trên Website công khai
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `PUT` |
| **Endpoint** | `/api/admin/staff/{id}/toggle-display` |
| **Đầu vào** | Path Variable `id` *(Profile ID)* |
| **Mô tả** | Bật hoặc ẩn hồ sơ của nhân sự khỏi trang công khai (Ví dụ: khi đi du lịch hoặc tạm nghỉ) |

**Mẫu Request:**
```
PUT /api/admin/staff/1/toggle-display
```

---

### 16. Đặt lại mật khẩu tài khoản nhân viên
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `POST` |
| **Endpoint** | `/api/admin/staff/{id}/reset-password` |
| **Đầu vào** | Path `id`, Query Param `newPassword` *(bắt buộc)* |
| **Mô tả** | Thiết lập mật khẩu mới cho nhân sự khi họ quên hoặc cần cấp lại mật khẩu |

**Mẫu Request:**
```
POST /api/admin/staff/1/reset-password?newPassword=NewSecurePass123
```

---

### 17. Quản lý tổng hợp tệp khách hàng (có phân trang)
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/admin/customers` |
| **Đầu vào** | - Query Param `search` *(tùy chọn, tìm theo tên hoặc SĐT)*<br>- Query Param `page` *(mặc định 0)*<br>- Query Param `size` *(mặc định 10)* |
| **Mô tả** | Tổng hợp lịch sử đặt lịch từ trước đến nay của khách hàng, tổng số tiền họ đã thanh toán đủ |

**Mẫu Request:**
```
GET /api/admin/customers?search=Nguyễn
```

**Mẫu Response (`200 OK`):**
```json
[
  {
    "customerName": "Nguyễn Thị Mai",
    "customerPhone": "0901234567",
    "customerEmail": "mai.nguyen@gmail.com",
    "totalBookings": 3,
    "totalSpent": 4499000.00,
    "bookingHistory": [
      { "bookingCode": "STB-20250720-A3FBX", "shootDate": "2025-08-20", "status": "COMPLETED", "totalAmount": 4499000.00 }
    ]
  }
]
```

---

## V. 📂 CMS NỘI DUNG & CẤU HÌNH HỆ THỐNG (CMS & CONFIG)

### 18. Xem danh sách tất cả gói chụp ảnh (Admin GET List - có phân trang)
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/admin/packages` |
| **Đầu vào** | - Query Param `page` *(mặc định 0)*<br>- Query Param `size` *(mặc định 10)* |
| **Mô tả** | Lấy danh sách đầy đủ tất cả gói chụp ảnh nghệ thuật để hiển thị tại bảng điều khiển CMS của Admin |

**Mẫu Request:**
```
GET /api/admin/packages
```

**Mẫu Response (`200 OK`):**
```json
[
  {
    "id": 1,
    "packageName": "GÓI CHỤP BASIC I",
    "slug": "goi-chup-basic-i",
    "price": 2699000.00,
    "shortDescription": "Gói cơ bản cá nhân",
    "thumbnailUrl": "https://res.cloudinary.com/.../basic1.jpg",
    "isActive": true
  }
]
```

---

### 19. Xem chi tiết một gói chụp ảnh để thực hiện cập nhật (Admin GET Detail)
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/admin/packages/{id}` |
| **Đầu vào** | Path Variable `id` *(bắt buộc)* |
| **Mô tả** | Lấy thông tin chi tiết đầy đủ của một gói dịch vụ chụp ảnh bằng ID để nạp vào biểu mẫu sửa đổi (Update Form) của Frontend |

**Mẫu Request:**
```
GET /api/admin/packages/1
```

**Mẫu Response (`200 OK`):**
```json
{
  "id": 1,
  "packageName": "GÓI CHỤP BASIC I",
  "slug": "goi-chup-basic-i",
  "price": 2699000.00,
  "shortDescription": "Gói cơ bản cá nhân",
  "detailContent": "<p>02 Layout chụp ảnh...</p>",
  "layoutCount": 2,
  "outfitCount": 2,
  "editedPhotos": 10,
  "makeupPersonCount": 1,
  "thumbnailUrl": "https://res.cloudinary.com/.../basic1.jpg",
  "isActive": true
}
```

---

### 20. Tạo gói chụp ảnh mới
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `POST` |
| **Endpoint** | `/api/admin/packages` |
| **Đầu vào** | **JSON Mode**: `@RequestBody` JSON <br> **Multipart Mode**: `@RequestPart("package")` JSON + `@RequestPart("thumbnailFile")` File |
| **Mô tả** | Tạo gói dịch vụ nghệ thuật mới. Hệ thống tự động tạo slug tiếng Việt nếu bỏ trống. **Hỗ trợ tự động tải thumbnail lên Cloudinary.** |

---

### 21. Cập nhật gói chụp ảnh
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `PUT` |
| **Endpoint** | `/api/admin/packages/{id}` |
| **Đầu vào** | Path `id` + **JSON Mode** / **Multipart Mode** (tương tự API tạo) |
| **Mô tả** | Cập nhật cấu hình bảng giá, số lượng trang phục, layout trang điểm và ảnh bìa gói chụp |

---

### 22. Xóa gói chụp ảnh
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `DELETE` |
| **Endpoint** | `/api/admin/packages/{id}` |
| **Đầu vào** | Path Variable `id` |
| **Mô tả** | Xóa hoàn toàn một gói dịch vụ khỏi danh sách kinh doanh của studio |

---

### 23. Xem danh sách tất cả Concept (Admin GET List - có phân trang)
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/admin/concepts` |
| **Đầu vào** | - Query Param `page` *(mặc định 0)*<br>- Query Param `size` *(mặc định 10)* |
| **Mô tả** | Lấy toàn bộ danh sách concept chụp ảnh nghệ thuật để hiển thị quản lý tại trang Admin CMS |

**Mẫu Request:**
```
GET /api/admin/concepts
```

---

### 24. Xem chi tiết một Concept để thực hiện cập nhật (Admin GET Detail)
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/admin/concepts/{id}` |
| **Đầu vào** | Path Variable `id` *(bắt buộc)* |
| **Mô tả** | Lấy chi tiết thông tin đầy đủ của một bối cảnh Concept bằng ID để nạp trước vào Form chỉnh sửa |

**Mẫu Request:**
```
GET /api/admin/concepts/1
```

**Mẫu Response (`200 OK`):**
```json
{
  "id": 1,
  "title": "SWEET ANGEL",
  "slug": "sweet-angel",
  "conceptType": "BEAUTY",
  "thumbnailUrl": "https://res.cloudinary.com/.../sweet.jpg",
  "description": "Mô tả concept...",
  "status": "PUBLISHED"
}
```

---

### 25. Tạo Concept chụp ảnh mới
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `POST` |
| **Endpoint** | `/api/admin/concepts` |
| **Đầu vào** | **JSON Mode**: `@RequestBody` JSON <br> **Multipart Mode**: `@RequestPart("concept")` JSON + `@RequestPart("thumbnailFile")` File |
| **Mô tả** | Tạo bối cảnh concept chụp ảnh mới. Tự động sinh slug thông minh. **Hỗ trợ tự động upload ảnh bìa lên Cloudinary.** |

---

### 26. Cập nhật Concept nghệ thuật
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `PUT` |
| **Endpoint** | `/api/admin/concepts/{id}` |
| **Đầu vào** | Path `id` + **JSON Mode** / **Multipart Mode** (tương tự API tạo) |
| **Mô tả** | Điều chỉnh thông tin mô tả, chủ đề hoặc ảnh bìa của Concept |

---

### 27. Xóa Concept nghệ thuật
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `DELETE` |
| **Endpoint** | `/api/admin/concepts/{id}` |
| **Đầu vào** | Path Variable `id` |
| **Mô tả** | Xóa Concept và các tài nguyên liên quan |

---

### 28. Gán ảnh vào bộ sưu tập Portfolio của Concept
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `POST` |
| **Endpoint** | `/api/admin/concepts/{id}/images` |
| **Đầu vào** | Path `id`, Query Params: `file` *(MultipartFile, tùy chọn)*, `imageUrl` *(tùy chọn)*, `sortOrder` *(mặc định 0)* |
| **Mô tả** | Thêm một hình ảnh mới vào album tác phẩm của Concept đó. **Tự động đẩy lên Cloudinary** nếu truyền file nhị phân trực tiếp |

---

### 29. Xóa ảnh khỏi album Portfolio của Concept
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `DELETE` |
| **Endpoint** | `/api/admin/concepts/images/{imageId}` |
| **Đầu vào** | Path Variable `imageId` |
| **Mô tả** | Gỡ bỏ vĩnh viễn hình ảnh đó khỏi album tác phẩm nghệ thuật |

---

### 30. Sắp xếp thứ tự hiển thị ảnh Concept (Drag and Drop)
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `PUT` |
| **Endpoint** | `/api/admin/concepts/images/sort` |
| **Đầu vào** | Request Body chứa mảng ID hình ảnh (`List<Long>`) |
| **Mô tả** | Nhận mảng các ID hình ảnh từ Frontend sau hành động kéo thả (drag and drop) và tự động thiết lập lại thứ tự `sort_order` tăng dần liên tục |

---

### 31. Xem danh sách tất cả bài viết Blog (Admin GET List - có phân trang)
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/admin/blogs` |
| **Đầu vào** | - Query Param `page` *(mặc định 0)*<br>- Query Param `size` *(mặc định 10)* |
| **Mô tả** | Trả về danh sách tất cả bài viết Blog phục vụ cho bảng quản trị bài viết CMS của Admin |

**Mẫu Request:**
```
GET /api/admin/blogs
```

---

### 32. Xem chi tiết bài viết Blog để thực hiện cập nhật (Admin GET Detail)
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/admin/blogs/{id}` |
| **Đầu vào** | Path Variable `id` *(bắt buộc)* |
| **Mô tả** | Lấy thông tin chi tiết đầy đủ của một bài viết Blog bằng ID để nạp trước thông tin vào biểu mẫu cập nhật |

**Mẫu Request:**
```
GET /api/admin/blogs/1
```

**Mẫu Response (`200 OK`):**
```json
{
  "id": 1,
  "title": "5 mẹo tạo dáng tự nhiên khi chụp ảnh Beauty",
  "slug": "meo-tao-dang-tu-nhien",
  "thumbnailUrl": "https://res.cloudinary.com/.../blog1.jpg",
  "content": "<h2>1. Đừng cứng nhắc...</h2>",
  "status": "PUBLISHED",
  "relatedConcept": {
    "id": 1,
    "title": "SWEET ANGEL"
  }
}
```

---

### 33. Tạo bài viết Blog mới
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `POST` |
| **Endpoint** | `/api/admin/blogs` |
| **Đầu vào** | **JSON Mode**: `@RequestBody` JSON + Query Param `conceptId` *(opt)* <br> **Multipart Mode**: `@RequestPart("blog")` JSON + Query `conceptId` + `@RequestPart("thumbnailFile")` File |
| **Mô tả** | Đăng tải bài viết blog chia sẻ mới. Tự động sinh slug thông minh. **Hỗ trợ tự động upload ảnh bìa bài viết lên Cloudinary.** |

---

### 34. Cập nhật bài viết Blog
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `PUT` |
| **Endpoint** | `/api/admin/blogs/{id}` |
| **Đầu vào** | Path `id` + **JSON Mode** / **Multipart Mode** (tương tự API tạo) |
| **Mô tả** | Cập nhật tiêu đề, nội dung HTML bài viết, ảnh đại diện mới hoặc thay đổi Concept liên kết |

---

### 35. Xóa bài viết Blog
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `DELETE` |
| **Endpoint** | `/api/admin/blogs/{id}` |
| **Đầu vào** | Path Variable `id` |
| **Mô tả** | Xóa bài viết Blog khỏi hệ thống |

---

### 36. Tạo feedback câu chuyện khách hàng (Before/After)
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `POST` |
| **Endpoint** | `/api/admin/stories` |
| **Đầu vào** | **JSON Mode**: `@RequestBody` JSON <br> **Multipart Mode**: `@RequestPart("story")` JSON + `@RequestPart("avatarFile")` File + `@RequestPart("imageAfterFile")` File |
| **Mô tả** | Tạo feedback khách hàng. **Backend tự up 2 file ảnh (trước/sau) lên Cloudinary.** |

---

### 37. Cập nhật câu chuyện khách hàng
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `PUT` |
| **Endpoint** | `/api/admin/stories/{id}` |
| **Đầu vào** | Path `id` + **JSON Mode** / **Multipart Mode** (tương tự API tạo) |
| **Mô tả** | Cập nhật tên, nội dung hoặc thay đổi 2 ảnh Before/After thông qua tự upload Cloudinary |

---

### 38. Xóa câu chuyện khách hàng
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `DELETE` |
| **Endpoint** | `/api/admin/stories/{id}` |
| **Đầu vào** | Path Variable `id` |
| **Mô tả** | Xóa câu chuyện khách hàng khỏi trang danh mục feedback |

---

### 39. Xem thông tin Studio hiện tại (Admin GET Detail)
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/admin/info` |
| **Đầu vào** | Không có |
| **Mô tả** | Lấy cấu hình thông tin tĩnh hiện tại của Studio để nạp trước vào biểu mẫu sửa đổi. |

---

### 40. Cập nhật thông tin cấu hình Studio tĩnh
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `PUT` |
| **Endpoint** | `/api/admin/info` |
| **Đầu vào** | **JSON Mode**: `@RequestBody` JSON <br> **Multipart Mode**: `@RequestPart("info")` JSON + `@RequestPart("logoFile")` File + `@RequestPart("bannerFile")` File |
| **Mô tả** | Cập nhật cấu hình cơ bản của studio. **Tự động upload ảnh logo/banner nhị phân lên Cloudinary.** |

---

### 41. API tải tệp tin độc lập lên đám mây Cloudinary
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `POST` |
| **Endpoint** | `/api/admin/upload` |
| **Đầu vào** | Query Params: `file` *(MultipartFile, bắt buộc)*, `folder` *(mặc định "studio")* |
| **Mô tả** | Endpoint độc lập tiếp nhận mọi tệp tin hình ảnh nhị phân, đẩy lên Cloudinary và trả về chuỗi secure URL CDN chất lượng cao |

---

## 📊 Bảng tổng hợp nhanh (Admin APIs Matrix)

| #   | Method | Endpoint | Dữ liệu Đầu vào | Chế độ Cloudinary tự động |
|-----|---|---|---|---|
| 1.  | `GET` | `/api/admin/bookings` | `?page=&size=&status=` | — |
| 2.  | `GET` | `/api/admin/bookings/{id}` | Path: `id` | — |
| 3.  | `PUT` | `/api/admin/bookings/{id}/status` | `?status=&note=` | — (Tự động gửi email) |
| 4.  | `PUT` | `/api/admin/bookings/{id}/payment` | `?status=&method=` | — (Tự động gửi email) |
| 5.  | `POST` | `/api/admin/bookings/{id}/assign` | `?photographerId=&makeupId=` | — (Tự động gửi email) |
| 6.  | `GET` | `/api/admin/bookings/{id}/history` | Path: `id` | — (Đã miễn dịch lỗi JSON) |
| 7.  | `GET` | `/api/admin/post-productions` | `?status=&page=&size=` | — (Đã miễn dịch lỗi JSON) |
| 8.  | `PUT` | `/api/admin/bookings/{id}/post-production` | JSON Body 4 fields | — (Đã miễn dịch lỗi JSON) |
| 9.  | `GET` | `/api/admin/dashboard/revenue` | `?startDate=&endDate=` | — |
| 10. | `GET` | `/api/admin/dashboard/statistics` | — | — |
| 11. | `GET` | `/api/admin/staff` | `?page=&size=` | — |
| 12. | `POST` | `/api/admin/staff` | JSON / Multipart | ✅ `avatarFile` (avatars) |
| 13. | `PUT` | `/api/admin/staff/{id}` | Path + JSON / Multipart | ✅ `avatarFile` (avatars) |
| 14. | `PUT` | `/api/admin/staff/{id}/toggle-active` | Path: `id` | — |
| 15. | `PUT` | `/api/admin/staff/{id}/toggle-display` | Path: `id` | — |
| 16. | `POST` | `/api/admin/staff/{id}/reset-password` | Path + `?newPassword=` | — |
| 17. | `GET` | `/api/admin/customers` | `?search=&page=&size=` | — |
| 18. | `GET` | `/api/admin/packages` | `?page=&size=` | — |
| 19. | `GET` | `/api/admin/packages/{id}` | Path: `id` | — (Xem chi tiết để sửa) |
| 20. | `POST` | `/api/admin/packages` | JSON / Multipart | ✅ `thumbnailFile` (packages) |
| 21. | `PUT` | `/api/admin/packages/{id}` | Path + JSON / Multipart | ✅ `thumbnailFile` (packages) |
| 22. | `DELETE`| `/api/admin/packages/{id}` | Path: `id` | — |
| 23. | `GET` | `/api/admin/concepts` | `?page=&size=` | — |
| 24. | `GET` | `/api/admin/concepts/{id}` | Path: `id` | — (Xem chi tiết để sửa) |
| 25. | `POST` | `/api/admin/concepts` | JSON / Multipart | ✅ `thumbnailFile` (concepts) |
| 26. | `PUT` | `/api/admin/concepts/{id}` | Path + JSON / Multipart | ✅ `thumbnailFile` (concepts) |
| 27. | `DELETE`| `/api/admin/concepts/{id}` | Path: `id` | — |
| 28. | `POST` | `/api/admin/concepts/{id}/images` | Path + Multipart / URL | ✅ `file` (concepts) |
| 29. | `DELETE`| `/api/admin/concepts/images/{imageId}` | Path: `imageId` | — |
| 30. | `PUT` | `/api/admin/concepts/images/sort` | Array ID `[1, 2, 3...]` | — |
| 31. | `GET` | `/api/admin/blogs` | `?page=&size=` | — |
| 32. | `GET` | `/api/admin/blogs/{id}` | Path: `id` | — (Xem chi tiết để sửa) |
| 33. | `POST` | `/api/admin/blogs` | JSON / Multipart | ✅ `thumbnailFile` (blogs) |
| 34. | `PUT` | `/api/admin/blogs/{id}` | Path + JSON / Multipart | ✅ `thumbnailFile` (blogs) |
| 35. | `DELETE`| `/api/admin/blogs/{id}` | Path: `id` | — |
| 36  | `POST` | `/api/admin/stories` | JSON / Multipart | ✅ `avatarFile`, `imageAfterFile` |
| 37  | `PUT` | `/api/admin/stories/{id}` | Path + JSON / Multipart | ✅ `avatarFile`, `imageAfterFile` |
| 38  | `DELETE`| `/api/admin/stories/{id}` | Path: `id` | — |
| 39  | `GET` | `/api/admin/info` | — | — (Xem thông tin để sửa) |
| 40  | `PUT` | `/api/admin/info` | JSON / Multipart | ✅ `logoFile`, `bannerFile` (studio) |
| 41  | `POST` | `/api/admin/upload` | Multipart file | ✅ `file` (tùy chọn thư mục) |

---

## 🔄 Vòng đời quản lý Nghiệp vụ Admin điển hình

```
Khởi động ngày làm việc
       │
       ├─ [10] Xem thống kê Dashboard nhanh → Biết số đơn chờ duyệt
       ├─ [1] Duyệt danh sách booking chờ xác nhận
       │    └─ [3] Xác nhận đơn (CONFIRMED) hoặc [4] Cập nhật Payment chuyển CONFIRMED
       │
       ├─ [5] Phân công thợ chụp & thợ makeup (Tránh kẹt ca, chuyển ASSIGNED)
       │    │
       │    ▼ (Thực hiện buổi chụp hình nghệ thuật tại Studio)
       │
       ├─ [8] Ghi nhận tiến độ Hậu kỳ (EDITING -> DELIVERED kèm link Drive ảnh)
       │    └─ Hệ thống tự động gửi email bàn giao link ảnh thành công tới Khách hàng
       │
       ├─ [11][12][13] CRUD Quản lý nhân viên & Bật hiển thị hồ sơ năng lực
       ├─ [18] -> [40] Thêm mới bảng giá, tạo concept, tải ảnh Portfolio, viết Blog, đăng feedback, xem & sửa cấu hình studio
       │
       └─ [9] Xem báo cáo doanh thu cuối ngày (Revenue Report)
```

---


