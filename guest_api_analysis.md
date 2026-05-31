# 📡 API Documentation — GUEST Role (12 Endpoints)

> **Base URL:** `http://localhost:8080`  
> **Prefix:** `/api/studio/**`  
> **Xác thực:** ❌ Không yêu cầu (Public - permitAll)  
> **Content-Type:** `application/json`

## 1. 🏠 Xem thông tin Studio
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/studio/info` |
| **Đầu vào** | Không có |
| **Mô tả** | Lấy toàn bộ thông tin cấu hình của Studio: tên, logo, banner, video giới thiệu, địa chỉ, SĐT, email, Facebook, Zalo, Google Maps, quy trình làm việc |

**Mẫu Response (`200 OK`):**
```json
{
  "id": 1,
  "studioName": "Nic.w Studio & Concept",
  "logoUrl": "https://res.cloudinary.com/.../logo.jpg",
  "bannerUrl": "https://res.cloudinary.com/.../banner.jpg",
  "address": "123 Nguyễn Văn Linh, Đà Nẵng",
  "phone": "0905753535",
  "email": "nicw.studio@gmail.com",
  "facebookUrl": "https://www.facebook.com/nicwstudioconcept",
  "zaloUrl": "https://zalo.me/0905753535",
  "youtubeUrl": "https://youtube.com/@nicwstudio",
  "introVideoUrl": "https://player.cloudinary.com/embed/?public_id=xxx",
  "introduction": "NIC.W STUDIO & CONCEPT là studio chuyên về ảnh beauty...",
  "workingProcess": "Bước 1: Tư vấn gói dịch vụ → Bước 2: Đặt lịch → ...",
  "googleMapUrl": "https://maps.app.goo.gl/..."
}
```

## 2. 🎨 Xem danh sách Concept (có lọc chủ đề & phân trang)
| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/studio/concepts` |
| **Đầu vào** | - Query Param `type` *(tùy chọn)*<br>- Query Param `page` *(mặc định 0)*<br>- Query Param `size` *(mặc định 10)* |
| **Mô tả** | Lấy danh sách các concept đang PUBLISHED. Hỗ trợ lọc theo chủ đề và phân trang |

**Các giá trị `type` hợp lệ:**
`BEAUTY` `COUPLE` `BIRTHDAY` `FAMILY` `OUTDOOR` `EVENT` `OTHER`

**Mẫu Request:**
```
GET /api/studio/concepts
GET /api/studio/concepts?type=BEAUTY
GET /api/studio/concepts?type=COUPLE
```

**Mẫu Response (`200 OK`):**
```json
[
  {
    "id": 1,
    "title": "SWEET ANGEL",
    "slug": "sweet-angel",
    "conceptType": "BEAUTY",
    "thumbnailUrl": "https://res.cloudinary.com/.../sweet-angel.jpg",
    "description": "Sự nhẹ nhàng của lông vũ và ngọt ngào của màu trắng",
    "status": "PUBLISHED",
    "createdAt": "2025-01-15T09:00:00"
  },
  {
    "id": 2,
    "title": "Tình Yêu Như Khu Rừng Bất Tận",
    "slug": "tinh-yeu-khu-rung",
    "conceptType": "COUPLE",
    "thumbnailUrl": "https://res.cloudinary.com/.../couple.jpg",
    "description": "Tình yêu của họ là cả một khu rừng hoang sơ...",
    "status": "PUBLISHED",
    "createdAt": "2025-01-20T10:00:00"
  }
]
```

---

## 3. 🖼️ Xem chi tiết Concept + Bộ sưu tập ảnh (Có Ê-kíp thực hiện)

| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/studio/concepts/{slug}` |
| **Đầu vào** | Path Variable `slug` |
| **Mô tả** | Xem chi tiết concept, toàn bộ bộ sưu tập ảnh sắp xếp theo thứ tự, và danh sách ê-kíp nhân sự (credits) đã thực hiện bối cảnh bộ ảnh này để tăng độ uy tín. |

**Mẫu Request:**
```
GET /api/studio/concepts/sweet-angel
```

**Mẫu Response (`200 OK`):**
```json
{
  "id": 1,
  "title": "SWEET ANGEL",
  "slug": "sweet-angel",
  "conceptType": "BEAUTY",
  "thumbnailUrl": "https://res.cloudinary.com/.../thumb.jpg",
  "description": "Sự nhẹ nhàng của lông vũ và ngọt ngào của màu trắng",
  "status": "PUBLISHED",
  "createdAt": "2025-01-15T09:00:00",
  "images": [
    { "id": 1, "imageUrl": "https://res.cloudinary.com/.../img1.jpg", "sortOrder": 0 },
    { "id": 2, "imageUrl": "https://res.cloudinary.com/.../img2.jpg", "sortOrder": 1 }
  ],
  "credits": [
    { "fullName": "Huỳnh Ly Na", "role": "PHOTOGRAPHER" },
    { "fullName": "Ly Ly", "role": "MAKEUP" }
  ]
}
```

**Lỗi (`404`):**
```json
{ "error": "Concept not found: sweet-angel-xyz" }
```

---

## 4. 💼 Xem danh sách gói dịch vụ (có phân trang)

| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/studio/packages` |
| **Đầu vào** | - Query Param `page` *(mặc định 0)*<br>- Query Param `size` *(mặc định 10)* |
| **Mô tả** | Lấy danh sách tất cả gói chụp ảnh đang kinh doanh (`is_active = true`), hỗ trợ phân trang |

**Mẫu Request:**
```
GET /api/studio/packages
```

**Mẫu Response (`200 OK`):**
```json
[
  {
    "id": 1,
    "packageName": "GÓI CHỤP BASIC I",
    "slug": "goi-chup-basic-i",
    "price": 2699000.00,
    "shortDescription": "Gói cơ bản dành cho cá nhân, làm đẹp nhẹ nhàng",
    "detailContent": "<p>02 Layout chụp ảnh...</p>",
    "layoutCount": 2,
    "outfitCount": 2,
    "editedPhotos": 10,
    "makeupPersonCount": 1,
    "thumbnailUrl": "https://res.cloudinary.com/.../basic1.jpg",
    "isActive": true
  },
  {
    "id": 2,
    "packageName": "GÓI CHỤP LUXURY",
    "slug": "goi-chup-luxury",
    "price": 4499000.00,
    "shortDescription": "Gói cao cấp kèm trang phục của Studio",
    "detailContent": "<p>02 Layout chụp ảnh, trang phục Studio...</p>",
    "layoutCount": 2,
    "outfitCount": 2,
    "editedPhotos": 12,
    "makeupPersonCount": 1,
    "thumbnailUrl": "https://res.cloudinary.com/.../luxury.jpg",
    "isActive": true
  }
]
```

---

## 5. 📋 Xem chi tiết gói dịch vụ

| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/studio/packages/{slug}` |
| **Đầu vào** | Path Variable `slug` |
| **Mô tả** | Xem chi tiết đầy đủ nội dung một gói chụp ảnh |

**Mẫu Request:**
```
GET /api/studio/packages/goi-chup-luxury
```

**Mẫu Response (`200 OK`):**
```json
{
  "id": 2,
  "packageName": "GÓI CHỤP LUXURY",
  "slug": "goi-chup-luxury",
  "price": 4499000.00,
  "shortDescription": "Gói cao cấp kèm trang phục của Studio",
  "detailContent": "<ul><li>02 Layout chụp ảnh</li><li>02 Trang phục (Đã bao gồm trang phục tại Studio)</li><li>12 Ảnh chỉnh sửa</li><li>Makeup & Hairstylist</li><li>Stylist Posing</li></ul>",
  "layoutCount": 2,
  "outfitCount": 2,
  "editedPhotos": 12,
  "makeupPersonCount": 1,
  "thumbnailUrl": "https://res.cloudinary.com/.../luxury.jpg",
  "isActive": true
}
```

---

## 6. 👥 Xem đội ngũ nhân sự (có phân trang)

| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/studio/staff` |
| **Đầu vào** | - Query Param `role` *(tùy chọn)*<br>- Query Param `page` *(mặc định 0)*<br>- Query Param `size` *(mặc định 10)* |
| **Mô tả** | Lấy danh sách nhân sự đang hiển thị trên website. Lọc theo vai trò và hỗ trợ phân trang |

**Các giá trị `role` hợp lệ:** `PHOTOGRAPHER` `MAKEUP`

**Mẫu Request:**
```
GET /api/studio/staff
GET /api/studio/staff?role=PHOTOGRAPHER
GET /api/studio/staff?role=MAKEUP
```

**Mẫu Response (`200 OK`):**
```json
[
  {
    "profileId": 1,
    "userId": 2,
    "fullName": "Huỳnh Ly Na",
    "roleName": "PHOTOGRAPHER",
    "avatarUrl": "https://res.cloudinary.com/.../avatar.jpg",
    "bio": "Một cô gái đầy đam mê và nhiệt huyết với chụp ảnh",
    "experienceDetail": "Chuyên chụp ảnh Beauty, Couple, Gia đình...",
    "yearsOfExperience": 5,
    "facebookUrl": "https://facebook.com/huynh.lyna",
    "instagramUrl": "https://instagram.com/huynh.lyna",
    "tiktokUrl": null
  },
  {
    "profileId": 2,
    "userId": 5,
    "fullName": "Ly Ly",
    "roleName": "MAKEUP",
    "avatarUrl": "https://res.cloudinary.com/.../lyly.jpg",
    "bio": "Nghệ sĩ trang điểm với hơn 4 năm kinh nghiệm",
    "experienceDetail": "Chuyên trang điểm Beauty, Cô dâu, Sân khấu",
    "yearsOfExperience": 4,
    "facebookUrl": null,
    "instagramUrl": "https://instagram.com/lyly.makeup",
    "tiktokUrl": "https://tiktok.com/@lyly.makeup"
  }
]
```

---

## 7. 📝 Xem danh sách bài viết Blog (có phân trang)

| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/studio/blogs` |
| **Đầu vào** | - Query Param `page` *(mặc định 0)*<br>- Query Param `size` *(mặc định 10)* |
| **Mô tả** | Lấy danh sách bài viết đã PUBLISHED, sắp xếp mới nhất trước, hỗ trợ phân trang |

**Mẫu Request:**
```
GET /api/studio/blogs
```

**Mẫu Response (`200 OK`):**
```json
[
  {
    "id": 1,
    "title": "5 mẹo tạo dáng tự nhiên khi chụp ảnh Beauty",
    "slug": "meo-tao-dang-tu-nhien",
    "thumbnailUrl": "https://res.cloudinary.com/.../blog1.jpg",
    "status": "PUBLISHED",
    "createdAt": "2025-06-01T08:00:00",
    "updatedAt": "2025-06-01T08:00:00",
    "relatedConceptId": 1,
    "relatedConceptTitle": "SWEET ANGEL",
    "relatedConceptSlug": "sweet-angel"
  },
  {
    "id": 2,
    "title": "Nên mặc gì khi đi chụp ảnh cặp đôi?",
    "slug": "mac-gi-khi-chup-anh-cap-doi",
    "thumbnailUrl": "https://res.cloudinary.com/.../blog2.jpg",
    "status": "PUBLISHED",
    "createdAt": "2025-05-28T10:00:00",
    "updatedAt": "2025-05-28T10:00:00",
    "relatedConceptId": 2,
    "relatedConceptTitle": "Tình Yêu Như Khu Rừng Bất Tận",
    "relatedConceptSlug": "tinh-yeu-khu-rung"
  }
]
```

---

## 8. 📖 Đọc chi tiết bài viết Blog

| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/studio/blogs/{slug}` |
| **Đầu vào** | Path Variable `slug` |
| **Mô tả** | Đọc nội dung đầy đủ bài viết, kèm gợi ý concept liên quan |

**Mẫu Request:**
```
GET /api/studio/blogs/meo-tao-dang-tu-nhien
```

**Mẫu Response (`200 OK`):**
```json
{
  "id": 1,
  "title": "5 mẹo tạo dáng tự nhiên khi chụp ảnh Beauty",
  "slug": "meo-tao-dang-tu-nhien",
  "thumbnailUrl": "https://res.cloudinary.com/.../blog1.jpg",
  "content": "<h2>1. Đừng cứng nhắc...</h2><p>Hãy tưởng tượng bạn đang...</p>",
  "status": "PUBLISHED",
  "createdAt": "2025-06-01T08:00:00",
  "updatedAt": "2025-06-01T08:00:00",
  "relatedConcept": {
    "id": 1,
    "title": "SWEET ANGEL",
    "slug": "sweet-angel",
    "conceptType": "BEAUTY",
    "thumbnailUrl": "https://res.cloudinary.com/.../thumb.jpg",
    "description": "Sự nhẹ nhàng của lông vũ...",
    "status": "PUBLISHED",
    "createdAt": "2025-01-15T09:00:00"
  }
}
```

---

## 9. ⭐ Xem đánh giá khách hàng (Before/After - có phân trang)

| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/studio/stories` |
| **Đầu vào** | - Query Param `page` *(mặc định 0)*<br>- Query Param `size` *(mặc định 10)* |
| **Mô tả** | Lấy feedback của khách hàng có `is_displayed = true`, kèm ảnh Before & After, hỗ trợ phân trang |

**Mẫu Request:**
```
GET /api/studio/stories
```

**Mẫu Response (`200 OK`):**
```json
[
  {
    "id": 1,
    "customerName": "Chị Ngô Hoàng Mỹ",
    "avatarUrl": "https://res.cloudinary.com/.../before.jpg",
    "imageAfterUrl": "https://res.cloudinary.com/.../after.jpg",
    "storyContent": "Makeup siêu đẹp luôn á, team thân thiện và rất dễ thương!",
    "createdAt": "2025-05-15T14:00:00"
  },
  {
    "id": 2,
    "customerName": "Chị Hồ Thu Hà",
    "avatarUrl": "https://res.cloudinary.com/.../before2.jpg",
    "imageAfterUrl": "https://res.cloudinary.com/.../after2.jpg",
    "storyContent": "Cảm ơn team Nic.w đã rất chu đáo và tận tâm, rất ưng ý!",
    "createdAt": "2025-05-10T16:00:00"
  }
]
```

---

## 10. 📅 Kiểm tra lịch chụp còn trống (Tích hợp bối cảnh & sức chứa động)

| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/studio/bookings/schedule` |
| **Đầu vào** | - Query Param `date` *(bắt buộc, format `yyyy-MM-dd`)*<br>- Query Param `packageId` *(tùy chọn)*: Để kiểm tra sức chứa động theo gói dịch vụ (có/không makeup).<br>- Query Param `conceptId` *(tùy chọn)*: Để kiểm tra bối cảnh chụp của Concept đã bị chiếm dụng chưa. |
| **Mô tả** | Trả về danh sách khung giờ trống thực tế và đã bận (bao gồm các ca đặt lịch + khóa giữ chỗ của người khác). Nếu truyền `conceptId` và `packageId`, hệ thống sẽ tính toán sức chứa chính xác tuyệt đối theo nghiệp vụ vận hành thực tế. |

**Khung giờ làm việc cố định của Studio:**
`07:30` `09:00` `10:30` `13:00` `14:30` `16:00`

**Mẫu Request:**
```
GET /api/studio/bookings/schedule?date=2026-08-23&packageId=2&conceptId=1
```

**Mẫu Response (`200 OK`):**
```json
{
  "date": "2026-08-23",
  "bookedSlots": ["09:00"],
  "availableSlots": ["07:30", "10:30", "13:00", "14:30", "16:00"]
}
```

---

## 10b. 🔒 Khóa giữ chỗ slot chụp tạm thời (Temporary Slot Hold)

| Thuộc tính | Giá trị |
|---|---|
| **Method** | `POST` |
| **Endpoint** | `/api/studio/bookings/hold` |
| **Đầu vào** | Request Body JSON |
| **Mô tả** | Khi khách chọn slot trên giao diện, gọi API này để giữ chỗ tạm thời trong **10 phút**. Trả về một `holdToken`. Nếu trong 10 phút khách không hoàn tất đặt lịch, slot tự động giải phóng. Để giữ chỗ thành công, hệ thống sẽ thực hiện kiểm tra kẹt bối cảnh Concept (tối đa 1 ca/concept/slot) và kẹt nhân sự. |

**Mẫu Request Body:**
```json
{
  "shootDate": "2026-08-23",
  "shootTimeSlot": "09:00:00",
  "conceptId": 1,
  "packageId": 2
}
```

**Mẫu Response (`200 OK`):**
```json
{
  "holdToken": "7d9c82e0-241f-4422-92e1-889adbe67b3c",
  "holdExpiredAt": "2026-05-31T02:04:43",
  "message": "Khóa giữ chỗ tạm thời thành công trong vòng 10 phút!"
}
```

---

## 11. 📸 Đặt lịch chụp ảnh trực tuyến

| Thuộc tính | Giá trị                                                                                                                                                                    |
|---|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Method** | `POST`                                                                                                                                                                     |
| **Endpoint** | `/api/studio/bookings`                                                                                                                                                     |
| **Đầu vào** | Request Body JSON *(bắt buộc)*                                                                                                                                             |
| **Mô tả** | Kiểm tra xem gói book có trùng lịch không nếu không Tạo đơn đặt lịch. Hệ thống tự động sinh Booking Code, tính tổng tiền từ gói dịch vụ, ghi lịch sử và gửi email xác nhận |

**Request Body:**
| Field | Kiểu | Bắt buộc | Mô tả |
|---|---|---|---|
| `customerName` | String | ✅ | Họ và tên khách hàng (tối đa 100 ký tự) |
| `customerEmail` | String (email) | ✅ | Email nhận xác nhận đặt lịch |
| `customerPhone` | String | ✅ | SĐT định dạng 0xxxxxxxxx |
| `customerFacebook` | String | ❌ | Link Facebook cá nhân |
| `shootDate` | Date (`yyyy-MM-dd`) | ✅ | Ngày chụp (**Phải lớn hơn hoặc bằng ngày hiện tại**, validate bằng `@FutureOrPresent`) |
| `shootTimeSlot` | Time (`HH:mm:ss`) | ✅ | Khung giờ chụp |
| `shootLocation` | String | ✅ | Địa điểm chụp |
| `packageId` | Long | ✅ | ID gói dịch vụ đã chọn |
| `conceptId` | Long | ✅ | ID concept mong muốn |
| `customerNotes` | String | ❌ | Ghi chú thêm cho studio (tối đa 1000 ký tự) |
| `holdToken` | String (UUID) | ❌ | Token giữ chỗ tạm thời (được trả về từ API `POST /hold`). Rất khuyến khích truyền lên để tránh bị tranh giành slot! |

**Mẫu Request Body:**
```json
{
  "customerName": "Nguyễn Thị Mai",
  "customerEmail": "mai.nguyen@gmail.com",
  "customerPhone": "0901234567",
  "customerFacebook": "https://facebook.com/mai.nguyen.123",
  "shootDate": "2026-08-20",
  "shootTimeSlot": "09:00:00",
  "shootLocation": "Studio - 123 Nguyễn Văn Linh, Đà Nẵng",
  "packageId": 2,
  "conceptId": 1,
  "customerNotes": "Em muốn chụp phong cách nhẹ nhàng, thiên về tông màu pastel",
  "holdToken": "7d9c82e0-241f-4422-92e1-889adbe67b3c"
}
```

**Mẫu Response (`200 OK`):**
```json
{
  "id": 15,
  "bookingCode": "STB-20260820-A3FBX",
  "customerName": "Nguyễn Thị Mai",
  "customerEmail": "mai.nguyen@gmail.com",
  "customerPhone": "0901234567",
  "shootDate": "2026-08-20",
  "shootTimeSlot": "09:00:00",
  "shootLocation": "Studio - 123 Nguyễn Văn Linh, Đà Nẵng",
  "packageName": "GÓI CHỤP LUXURY",
  "conceptTitle": "SWEET ANGEL",
  "totalAmount": 4499000.00,
  "bookingStatus": "PENDING",
  "paymentStatus": "UNPAID",
  "createdAt": "2026-07-20T22:15:30",
  "message": "Đặt lịch thành công! Email xác nhận đã được gửi tới mai.nguyen@gmail.com. Vui lòng lưu Mã đặt lịch để tra cứu."
}
```

**Lỗi slot đã đặt (`400`):**
```json
{ "error": "Khung giờ 09:00 ngày 2025-08-20 đã được đặt. Vui lòng chọn khung giờ khác." }
```

**Lỗi validation (`400`):**
```json
{
  "customerEmail": "Email không hợp lệ",
  "shootDate": "Ngày chụp phải là ngày trong tương lai",
  "packageId": "Vui lòng chọn gói dịch vụ"
}
```

---

## 12. 🔍 Tra cứu tiến độ xử lý Booking

| Thuộc tính | Giá trị |
|---|---|
| **Method** | `GET` |
| **Endpoint** | `/api/studio/bookings/lookup` |
| **Đầu vào** | Query Params `phone` + `code` *(bắt buộc)* |
| **Mô tả** | Khách nhập SĐT + Mã đặt lịch để tra cứu: trạng thái booking, thanh toán, nhân sự phân công, tiến độ hậu kỳ và link nhận ảnh |

**Mẫu Request:**
```
GET /api/studio/bookings/lookup?phone=0901234567&code=STB-20250720-A3FBX
```

**Mẫu Response — Đang chờ xác nhận (`200 OK`):**
```json
{
  "bookingCode": "STB-20250720-A3FBX",
  "customerName": "Nguyễn Thị Mai",
  "shootDate": "2025-08-20",
  "shootTimeSlot": "09:00:00",
  "shootLocation": "Studio - 123 Nguyễn Văn Linh, Đà Nẵng",
  "packageName": "GÓI CHỤP LUXURY",
  "conceptTitle": "SWEET ANGEL",
  "totalAmount": 4499000.00,
  "bookingStatus": "PENDING",
  "paymentStatus": "UNPAID",
  "assignedStaff": [],
  "productionStatus": null,
  "editedPhotoLink": null
}
```

**Mẫu Response — Đã phân công nhân sự (`200 OK`):**
```json
{
  "bookingCode": "STB-20250720-A3FBX",
  "customerName": "Nguyễn Thị Mai",
  "shootDate": "2025-08-20",
  "shootTimeSlot": "09:00:00",
  "shootLocation": "Studio - 123 Nguyễn Văn Linh, Đà Nẵng",
  "packageName": "GÓI CHỤP LUXURY",
  "conceptTitle": "SWEET ANGEL",
  "totalAmount": 4499000.00,
  "bookingStatus": "ASSIGNED",
  "paymentStatus": "DEPOSITED",
  "assignedStaff": [
    {
      "fullName": "Huỳnh Ly Na",
      "role": "PHOTOGRAPHER",
      "avatarUrl": "https://res.cloudinary.com/.../lyna.jpg"
    },
    {
      "fullName": "Ly Ly",
      "role": "MAKEUP",
      "avatarUrl": "https://res.cloudinary.com/.../lyly.jpg"
    }
  ],
  "productionStatus": null,
  "editedPhotoLink": null
}
```

**Mẫu Response — Đã bàn giao ảnh (`200 OK`):**
```json
{
  "bookingCode": "STB-20250720-A3FBX",
  "customerName": "Nguyễn Thị Mai",
  "shootDate": "2025-08-20",
  "shootTimeSlot": "09:00:00",
  "shootLocation": "Studio - 123 Nguyễn Văn Linh, Đà Nẵng",
  "packageName": "GÓI CHỤP LUXURY",
  "conceptTitle": "SWEET ANGEL",
  "totalAmount": 4499000.00,
  "bookingStatus": "COMPLETED",
  "paymentStatus": "PAID",
  "assignedStaff": [
    { "fullName": "Huỳnh Ly Na", "role": "PHOTOGRAPHER", "avatarUrl": "https://res.cloudinary.com/.../lyna.jpg" },
    { "fullName": "Ly Ly", "role": "MAKEUP", "avatarUrl": "https://res.cloudinary.com/.../lyly.jpg" }
  ],
  "productionStatus": "DELIVERED",
  "editedPhotoLink": "https://drive.google.com/drive/folders/abc123xyz"
}
```

**Lỗi sai SĐT (`400`):**
```json
{ "error": "Số điện thoại không khớp với mã đặt lịch." }
```

**Lỗi không tìm thấy (`400`):**
```json
{ "error": "Không tìm thấy lịch đặt với mã: STB-INVALID-CODE" }
```

---

## 📊 Bảng tổng hợp nhanh

| # | Method | Endpoint | Đầu vào | Kết quả |
|---|---|---|---|---|
| 1 | `GET` | `/api/studio/info` | — | Object thông tin studio |
| 2 | `GET` | `/api/studio/concepts` | `?type=&page=&size=` *(opt)* | Array danh sách concept |
| 3 | `GET` | `/api/studio/concepts/{slug}` | Path: `slug` | Object concept + array ảnh |
| 4 | `GET` | `/api/studio/packages` | `?page=&size=` *(opt)* | Array danh sách gói |
| 5 | `GET` | `/api/studio/packages/{slug}` | Path: `slug` | Object chi tiết gói |
| 6 | `GET` | `/api/studio/staff` | `?role=&page=&size=` *(opt)* | Array danh sách nhân sự |
| 7 | `GET` | `/api/studio/blogs` | `?page=&size=` *(opt)* | Array danh sách blog |
| 8 | `GET` | `/api/studio/blogs/{slug}` | Path: `slug` | Object bài viết + concept gợi ý |
| 9 | `GET` | `/api/studio/stories` | `?page=&size=` *(opt)* | Array feedback Before/After |
| 10 | `GET` | `/api/studio/bookings/schedule` | `?date=yyyy-MM-dd` *(bắt buộc)* | Object slot đã đặt + còn trống (đã lọc giữ chỗ) |
| 10b| `POST` | `/api/studio/bookings/hold` | JSON Body 2 fields | Giữ chỗ slot chụp tạm thời trong 10 phút |
| 11 | `POST` | `/api/studio/bookings` | JSON Body 10 or 11 fields | Đặt lịch trực tuyến (hỗ trợ `@FutureOrPresent` & `holdToken`) |
| 12 | `GET` | `/api/studio/bookings/lookup` | `?phone=&code=` *(bắt buộc)* | Object trạng thái đầy đủ |

---

## 🔄 Luồng trải nghiệm người dùng điển hình

```
Khách truy cập Website
       │
       ├─ [1] Xem thông tin Studio → Biết địa chỉ, SĐT, quy trình
       ├─ [2][3] Khám phá Concept → Chọn chủ đề chụp yêu thích (Có ê-kíp thực hiện)
       ├─ [4][5] Xem Gói dịch vụ → Quyết định chọn gói phù hợp
       ├─ [6] Xem Đội ngũ → Tăng niềm tin
       ├─ [9] Đọc Feedback Before/After → Quyết định đặt lịch
       │
       ├─ [10] Kiểm tra lịch trống → Chọn ngày & giờ phù hợp
       ├─ [10b] POST Khóa giữ chỗ tạm thời (Hold Slot) → Nhận holdToken trong 10 phút
       ├─ [11] POST Đặt lịch (gửi kèm holdToken) → Nhận Booking Code + Email xác nhận (Tự giải phóng hold)
       │
       └─ [12] Tra cứu tiến độ (bất kỳ lúc nào) → Xem trạng thái + link ảnh
```
