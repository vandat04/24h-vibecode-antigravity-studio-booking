# 📡 API Documentation — STAFF Role (5 Endpoints)

> **Base URL:** `http://localhost:8080`  
> **Prefix:** `/api/staff/**`  
> **Xác thực:** 🔐 Yêu cầu Header `Authorization: Bearer <JWT_TOKEN>`  
> **Phân quyền:** Chỉ chấp nhận tài khoản có Role `PHOTOGRAPHER` hoặc `MAKEUP` (hoặc `ADMIN` tối cao)  
> **Content-Type:** `application/json`

---

## 1. 📅 Xem danh sách lịch làm việc cá nhân

| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/staff/bookings` |
| **Đầu vào** | Không có |
| **Mô tả** | Lấy toàn bộ danh sách các ca làm việc (lịch chụp) mà nhân sự đang đăng nhập được Admin phân công (`booking_assignments`). Hỗ trợ hiển thị dạng Danh sách hoặc nạp vào dạng Lịch (Calendar) tuần/tháng. |

**Mẫu Request:**
```
GET /api/staff/bookings
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Mẫu Response (`200 OK`):**
```json
[
  {
    "id": 8,
    "bookingCode": "STB-20260531-C62BE",
    "customerName": "Nguyễn Thị Mai",
    "customerEmail": "mai.nguyen@gmail.com",
    "customerPhone": "0901234567",
    "customerFacebook": "https://facebook.com/mai.nguyen.123",
    "shootDate": "2026-08-23",
    "shootTimeSlot": "09:00:00",
    "shootLocation": "Studio - 123 Nguyễn Văn Linh, Đà Nẵng",
    "customerNotes": "Khách muốn chụp góc mặt bên trái, bối cảnh tone tối",
    "totalAmount": 1990000.00,
    "bookingStatus": "ASSIGNED",
    "paymentStatus": "DEPOSITED",
    "createdAt": "2026-05-30T17:15:30",
    "packageName": "Premium Beauty",
    "conceptTitle": "SWEET ANGEL",
    "assignedStaff": [
      { "staffId": 2, "fullName": "Huỳnh Ly Na", "role": "PHOTOGRAPHER" },
      { "staffId": 5, "fullName": "Ly Ly", "role": "MAKEUP" }
    ]
  }
]
```

**Lỗi chưa đăng nhập / Sai Token (`401 Unauthorized`):**
```json
{ "error": "Full authentication is required to access this resource" }
```

---

## 2. 🔍 Xem chi tiết ca làm việc (Có bảo mật chéo)

| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/staff/bookings/{id}` |
| **Đầu vào** | Path Variable `id` (ID của đơn hàng) |
| **Mô tả** | Xem thông tin chi tiết đầy đủ của một ca chụp. **Kiểm soát bảo mật chéo:** Nhân viên chỉ có quyền xem chi tiết ca chụp mà chính mình được phân công thực hiện. Nếu xem trộm lịch của thợ khác sẽ bị chặn đứng ngay lập tức. |

**Mẫu Request:**
```
GET /api/staff/bookings/8
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Mẫu Response (`200 OK`):**
```json
{
  "id": 8,
  "bookingCode": "STB-20260531-C62BE",
  "customerName": "Nguyễn Thị Mai",
  "customerEmail": "mai.nguyen@gmail.com",
  "customerPhone": "0901234567",
  "customerFacebook": "https://facebook.com/mai.nguyen.123",
  "shootDate": "2026-08-23",
  "shootTimeSlot": "09:00:00",
  "shootLocation": "Studio - 123 Nguyễn Văn Linh, Đà Nẵng",
  "customerNotes": "Khách bị dị ứng mỹ phẩm có cồn, muốn makeup tone cam đất bám da.",
  "totalAmount": 1990000.00,
  "bookingStatus": "ASSIGNED",
  "paymentStatus": "DEPOSITED",
  "createdAt": "2026-05-30T17:15:30",
  "packageName": "Premium Beauty",
  "conceptTitle": "SWEET ANGEL",
  "assignedStaff": [
    { "staffId": 2, "fullName": "Huỳnh Ly Na", "role": "PHOTOGRAPHER" },
    { "staffId": 5, "fullName": "Ly Ly", "role": "MAKEUP" }
  ]
}
```

**Lỗi truy cập trộm lịch thợ khác (`403 Forbidden`):**
```json
{ "error": "Access Denied: Bạn không có quyền truy cập ca làm việc này vì không được phân công." }
```

---

## 3. 💄 Xác nhận hoàn thành Makeup (Dành cho Chuyên viên trang điểm)

| Thuộc tính | Giá trị |
|---|---|
| **Method** | `POST` |
| **Endpoint** | `/api/staff/bookings/{id}/makeup-complete` |
| **Đầu vào** | Path Variable `id` (ID của đơn hàng) |
| **Mô tả** | Khi trang điểm xong cho khách hàng, Makeup Artist gọi API này để báo hiệu cho Photographer đồng hành biết khách đã sẵn sàng. Hệ thống sẽ tự động lưu vết audit log vào lịch sử đơn chụp. |

**Mẫu Request:**
```
POST /api/staff/bookings/8/makeup-complete
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Mẫu Response (`200 OK`):**
```
Xác nhận hoàn thành makeup thành công!
```

**Lỗi Photographer cố tình gọi API này (`403 Forbidden`):**
```json
{ "error": "Access Denied: Chỉ chuyên viên trang điểm (MAKEUP) mới được quyền thực hiện chức năng này." }
```

---

## 📸 4. Cập nhật tiến độ hậu kỳ & Bàn giao hình ảnh (Dành cho Nhiếp ảnh gia)

| Thuộc tính | Giá trị |
|---|---|
| **Method** | `PUT` |
| **Endpoint** | `/api/staff/bookings/{id}/post-production` |
| **Đầu vào** | JSON Body gồm các trường thông tin hậu kỳ |
| **Mô tả** | Nhiếp ảnh gia trực tiếp tải và cập nhật link ảnh thô (Google Drive/Dropbox), link ảnh đã chỉnh sửa (Edited), và chuyển trạng thái hậu kỳ. Khi chuyển sang trạng thái `DELIVERED`, hệ thống sẽ **tự động gửi email bàn giao ảnh đẹp mắt** cho khách hàng. Ngoài ra, hệ thống tự động đổi trạng thái đơn chụp sang `EDITING` nếu đơn đang ở trạng thái `ASSIGNED`. |

**Các giá trị `productionStatus` hợp lệ:**  
`UNPROCESSED` `EDITING` `WAITING_APPROVAL` `DELIVERED`

**Request Body:**
| Field | Kiểu | Bắt buộc | Mô tả |
|---|---|---|---|
| `productionStatus` | String | ✅ | Trạng thái tiến độ hậu kỳ hình ảnh |
| `rawPhotoLink` | String | ❌ | Link thư mục ảnh gốc/ảnh thô (Raw) |
| `editedPhotoLink` | String | ❌ | Link thư mục ảnh đã chỉnh sửa hoàn thiện |
| `note` | String | ❌ | Ghi chú thêm của thợ chỉnh sửa |

**Mẫu Request Body:**
```json
{
  "productionStatus": "DELIVERED",
  "rawPhotoLink": "https://drive.google.com/drive/folders/raw-folder-abc",
  "editedPhotoLink": "https://drive.google.com/drive/folders/edited-folder-xyz",
  "note": "Đã hoàn thành toàn bộ hình ảnh sắc nét, blend màu tone Hàn Quốc ngọt ngào"
}
```

**Mẫu Response (`200 OK`):**
```json
{
  "id": 12,
  "booking": {
    "id": 8,
    "bookingCode": "STB-20260531-C62BE",
    "customerName": "Nguyễn Thị Mai",
    "customerEmail": "mai.nguyen@gmail.com"
  },
  "productionStatus": "DELIVERED",
  "rawPhotoLink": "https://drive.google.com/drive/folders/raw-folder-abc",
  "editedPhotoLink": "https://drive.google.com/drive/folders/edited-folder-xyz",
  "note": "Đã hoàn thành toàn bộ hình ảnh sắc nét, blend màu tone Hàn Quốc ngọt ngào",
  "updatedBy": {
    "id": 2,
    "fullName": "Huỳnh Ly Na",
    "role": { "roleName": "PHOTOGRAPHER" }
  },
  "updatedAt": "2026-05-31T02:15:30"
}
```

---

## 🔐 5. Thay đổi mật khẩu cá nhân

| Thuộc tính | Giá trị |
|---|---|
| **Method** | `POST` |
| **Endpoint** | `/api/staff/profile/change-password` |
| **Đầu vào** | Request Body JSON (Mật khẩu cũ và mật khẩu mới) |
| **Mô tả** | Nhân sự (thợ chụp, makeup) tự thay đổi mật khẩu đăng nhập cá nhân để đảm bảo an toàn thông tin bảo mật. |

**Request Body:**
| Field | Kiểu | Bắt buộc | Mô tả |
|---|---|---|---|
| `oldPassword` | String | ✅ | Mật khẩu hiện tại |
| `newPassword` | String | ✅ | Mật khẩu mới mong muốn thiết lập |

**Mẫu Request Body:**
```json
{
  "oldPassword": "old-secure-password-123",
  "newPassword": "new-secure-password-456"
}
```

**Mẫu Response (`200 OK`):**
```
Thay đổi mật khẩu thành công!
```

**Lỗi nhập sai mật khẩu cũ (`400 Bad Request`):**
```json
{ "error": "Mật khẩu cũ không chính xác." }
```

---

## 📊 Bảng tổng hợp nhanh các API Staff

| # | Method | Endpoint | Quyền hạn truy cập | Kết quả trả về |
|---|---|---|---|---|
| 1 | `GET` | `/api/staff/bookings` | `PHOTOGRAPHER`, `MAKEUP` | Array danh sách lịch chụp cá nhân |
| 2 | `GET` | `/api/staff/bookings/{id}` | Lọc theo Phân công thực tế | Object chi tiết ca chụp và đồng nghiệp |
| 3 | `POST` | `/api/staff/bookings/{id}/makeup-complete` | Chỉ `MAKEUP` | Xác nhận hoàn tất trang điểm |
| 4 | `PUT` | `/api/staff/bookings/{id}/post-production` | Chỉ `PHOTOGRAPHER` | Cập nhật link ảnh hậu kỳ & gửi mail |
| 5 | `POST` | `/api/staff/profile/change-password` | `PHOTOGRAPHER`, `MAKEUP` | Thay đổi mật khẩu cá nhân |

---

## 🔄 Luồng phối hợp vận hành thực tế của Ê-kíp tại Studio

```
      [ Khách hàng đến Studio ]
                  │
                  ▼
       [1] MAKEUP ARTIST trang điểm
                  │
                  ├─ Xem gói chụp: outfitCount, layoutCount
                  └─ Xem ghi chú da của khách để chuẩn bị mỹ phẩm
                  │
                  ▼
       [2] POST Gọi API báo Hoàn thành Makeup
                  │ (Hệ thống ghi nhận & cập nhật tiến độ)
                  ▼
       [3] PHOTOGRAPHER nắm thông tin và Bắt đầu Chụp
                  │
                  ├─ Xem Concept mong muốn để set bối cảnh & ánh sáng
                  └─ Thực hiện bấm máy chụp hình
                  │
                  ▼
       [4] Chụp xong -> PHOTOGRAPHER bắt đầu xử lý Hậu kỳ
                  │
                  ├─ PUT Cập nhật link ảnh Raw (Đơn tự chuyển sang EDITING)
                  └─ Thực hiện blend màu sắc nét
                  │
                  ▼
       [5] PUT Cập nhật link ảnh Edited & chọn DELIVERED
                  │
                  └─ Hệ thống TỰ ĐỘNG gửi email chứa link ảnh bàn giao cho Khách hàng!
```
