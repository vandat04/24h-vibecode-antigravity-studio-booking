
USE studio_booking;
-- =====================================================
-- USERS : pass: 123123
-- =====================================================
INSERT INTO users (role_id, username, password_hash, full_name, email, phone) VALUES
-- ADMIN
(1,'admin','$2a$10$OwEVq/j00c/HhMfHUkJ9JuMVCZGv3XrmXYCWApMqtFO1HdHWN.SdG','Nguyen Minh Quan','admin@nicstudio.vn','0905000001'),
-- MAKEUP
(2,'makeup_linh','$2a$10$OwEVq/j00c/HhMfHUkJ9JuMVCZGv3XrmXYCWApMqtFO1HdHWN.SdG','Tran Ngoc Linh','linh@nicstudio.vn','0905000002'),
(2,'makeup_huong','$2a$10$OwEVq/j00c/HhMfHUkJ9JuMVCZGv3XrmXYCWApMqtFO1HdHWN.SdG','Le Thu Huong','huong@nicstudio.vn','0905000003'),
(2,'makeup_thao','$2a$10$OwEVq/j00c/HhMfHUkJ9JuMVCZGv3XrmXYCWApMqtFO1HdHWN.SdG','Pham Minh Thao','thao@nicstudio.vn','0905000004'),
-- PHOTOGRAPHER
(3,'photo_hai','$2a$10$OwEVq/j00c/HhMfHUkJ9JuMVCZGv3XrmXYCWApMqtFO1HdHWN.SdG','Nguyen Van Hai','hai@nicstudio.vn','0905000005'),
(3,'photo_khoa','$2a$10$OwEVq/j00c/HhMfHUkJ9JuMVCZGv3XrmXYCWApMqtFO1HdHWN.SdG','Tran Quoc Khoa','khoa@nicstudio.vn','0905000006'),
(3,'photo_tuan','$2a$10$OwEVq/j00c/HhMfHUkJ9JuMVCZGv3XrmXYCWApMqtFO1HdHWN.SdG','Le Anh Tuan','tuan@nicstudio.vn','0905000007'),
(3,'photo_nam','$2a$10$OwEVq/j00c/HhMfHUkJ9JuMVCZGv3XrmXYCWApMqtFO1HdHWN.SdG','Pham Hoang Nam','nam@nicstudio.vn','0905000008');

-- =====================================================
-- STAFF PROFILES
-- =====================================================
INSERT INTO staff_profiles(user_id, avatar_url, bio, experience_detail, years_of_experience, facebook_url, instagram_url, tiktok_url) VALUES
(2, '/uploads/staff/linh.jpg', 'Makeup Beauty chuyên nghiệp', 'Trang điểm beauty, kỷ yếu, couple', 5, 'https://facebook.com/linh', 'https://instagram.com/linh', 'https://tiktok.com/@linh'),
(3, '/uploads/staff/huong.jpg', 'Makeup phong cách Hàn Quốc', 'Chuyên makeup cô dâu và beauty', 6, 'https://facebook.com/huong', 'https://instagram.com/huong', 'https://tiktok.com/@huong'),
(4, '/uploads/staff/thao.jpg', 'Makeup trẻ trung', 'Beauty concept hiện đại', 4, 'https://facebook.com/thao', 'https://instagram.com/thao', 'https://tiktok.com/@thao'),
(5, '/uploads/staff/hai.jpg', 'Photographer chính', 'Beauty và Outdoor', 8, 'https://facebook.com/hai', 'https://instagram.com/hai', NULL),
(6, '/uploads/staff/khoa.jpg', 'Photographer', 'Chuyên Couple', 5, 'https://facebook.com/khoa', 'https://instagram.com/khoa', NULL),
(7, '/uploads/staff/tuan.jpg', 'Photographer', 'Beauty Studio', 6, 'https://facebook.com/tuan', 'https://instagram.com/tuan', NULL),
(8, '/uploads/staff/nam.jpg', 'Photographer', 'Outdoor và Event', 7, 'https://facebook.com/nam', 'https://instagram.com/nam', NULL);

-- =====================================================
-- STUDIO INFORMATION
-- =====================================================
INSERT INTO studio_information(studio_name, logo_url, banner_url, address, phone, email, facebook_url, zalo_url, youtube_url, intro_video_url, introduction, working_process, google_map_url ) VALUES
('Nic.W Studio', '/uploads/logo.png', '/uploads/banner.jpg', '123 Nguyen Van Linh, Hai Chau, Da Nang', '0905123456', 'contact@nicstudio.vn', 'https://facebook.com/nicstudio', 'https://zalo.me/nicstudio', 'https://youtube.com/nicstudio', 'https://youtube.com/watch?v=abc123', 'Studio chụp ảnh beauty hàng đầu Đà Nẵng.', 'Đặt lịch -> Tư vấn -> Makeup -> Chụp -> Hậu kỳ -> Bàn giao', 'https://maps.google.com');

-- =====================================================
-- CONCEPTS
-- =====================================================
INSERT INTO concepts(title, slug, concept_type, thumbnail_url, description) VALUES
('Beauty Hàn Quốc', 'beauty-han-quoc', 'BEAUTY', '/uploads/concepts/beauty1.jpg', 'Phong cách nhẹ nhàng'),
('Beauty Luxury', 'beauty-luxury', 'BEAUTY', '/uploads/concepts/beauty2.jpg', 'Beauty cao cấp'),
('Couple Sweet', 'couple-sweet', 'COUPLE', '/uploads/concepts/couple1.jpg', 'Concept cặp đôi'),
('Family Memory', 'family-memory', 'FAMILY', '/uploads/concepts/family1.jpg', 'Ảnh gia đình'),
('Birthday Party', 'birthday-party', 'BIRTHDAY', '/uploads/concepts/birthday1.jpg', 'Sinh nhật'),
('Beach Outdoor', 'beach-outdoor', 'OUTDOOR', '/uploads/concepts/outdoor1.jpg', 'Biển Đà Nẵng');

-- =====================================================
-- CONCEPT IMAGES
-- =====================================================
INSERT INTO concept_images(concept_id, image_url, sort_order) VALUES
(1, '/uploads/concepts/beauty1_1.jpg', 1),
(1, '/uploads/concepts/beauty1_2.jpg', 2),
(1, '/uploads/concepts/beauty1_3.jpg', 3),
(2, '/uploads/concepts/beauty2_1.jpg', 1),
(2, '/uploads/concepts/beauty2_2.jpg', 2),
(3, '/uploads/concepts/couple1_1.jpg', 1),
(3, '/uploads/concepts/couple1_2.jpg', 2),
(4, '/uploads/concepts/family1_1.jpg', 1),
(5, '/uploads/concepts/birthday1_1.jpg', 1),
(6, '/uploads/concepts/outdoor1_1.jpg', 1),
(6, '/uploads/concepts/outdoor1_2.jpg', 2);

-- =====================================================
-- SERVICE PACKAGES
-- =====================================================
INSERT INTO service_packages(package_name, slug, price, short_description, detail_content, layout_count, outfit_count, edited_photos, makeup_person_count, thumbnail_url ) VALUES
('Basic Beauty', 'basic-beauty', 990000, 'Beauty cơ bản', 'Bao gồm makeup và chụp studio', 1, 1, 5, 1, '/uploads/packages/basic.jpg' ),
('Premium Beauty', 'premium-beauty', 1990000, 'Beauty cao cấp', 'Beauty chuyên nghiệp', 2, 2, 10, 1, '/uploads/packages/premium.jpg' ),
('Luxury Beauty', 'luxury-beauty', 2990000, 'Beauty Luxury', 'Beauty cao cấp nhất', 3, 3, 20, 1, '/uploads/packages/luxury.jpg' ),
('Couple Package', 'couple-package', 2490000, 'Concept Couple', 'Chụp cặp đôi', 2, 2, 15, 2, '/uploads/packages/couple.jpg' ),
('Family Package', 'family-package', 3490000, 'Gia đình', 'Chụp gia đình', 3, 3, 20, 4, '/uploads/packages/family.jpg' );

-- =====================================================
-- BLOGS
-- =====================================================
INSERT INTO blogs( title, slug, thumbnail_url, content, related_concept_id, status ) VALUES
('Top 10 Concept Beauty 2026', 'top-10-concept-beauty-2026', '/uploads/blogs/blog1.jpg', 'Noi dung bai viet...', 1, 'PUBLISHED'),
('Kinh Nghiem Chup Couple', 'kinh-nghiem-chup-couple', '/uploads/blogs/blog2.jpg', 'Noi dung bai viet...', 3, 'PUBLISHED'),
('5 Dia Diem Chup Bien Da Nang', '5-dia-diem-bien-da-nang', '/uploads/blogs/blog3.jpg', 'Noi dung bai viet...', 6, 'PUBLISHED');

-- =====================================================
-- CUSTOMER STORIES
-- =====================================================
INSERT INTO customer_stories(customer_name, avatar_url, story_content) VALUES
('Nguyen Thi Mai', '/uploads/customers/c1.jpg', 'Dich vu rat tot'),
('Tran Quoc Bao', '/uploads/customers/c2.jpg', 'Anh dep hon mong doi'),
('Le Minh Chau', '/uploads/customers/c3.jpg', 'Makeup rat dep'),
('Pham Ngoc Han', '/uploads/customers/c4.jpg', 'Photographer rat nhiet tinh'),
('Vo Hoang Yen', '/uploads/customers/c5.jpg', 'Hau ky nhanh va dep');

-- =====================================================
-- BOOKINGS
-- =====================================================
INSERT INTO bookings(booking_code, customer_name, customer_email, customer_phone, shoot_date, shoot_time_slot, shoot_location, package_id, concept_id, total_amount, booking_status, payment_status ) VALUES
('BK0001','Nguyen Van A','a@gmail.com','0911111111', '2026-07-01','08:00:00', 'Nic.W Studio', 1,1, 990000, 'CONFIRMED', 'DEPOSITED'),
('BK0002','Tran Thi B','b@gmail.com','0911111112', '2026-07-01','14:00:00', 'Nic.W Studio', 2,2, 1990000, 'ASSIGNED', 'PAID'),
('BK0003','Le Van C','c@gmail.com','0911111113', '2026-07-02','08:00:00', 'My Khe Beach', 4,3, 2490000, 'SHOOTING', 'PAID'),
('BK0004','Pham Thi D','d@gmail.com','0911111114', '2026-07-03','09:00:00', 'Nic.W Studio', 5,4, 3490000, 'EDITING', 'PAID'),
('BK0005','Nguyen Thi E','e@gmail.com','0911111115', '2026-07-05','08:00:00', 'Nic.W Studio', 3,1, 2990000, 'COMPLETED', 'PAID');

-- =====================================================
-- BOOKING ASSIGNMENTS
-- =====================================================
INSERT INTO booking_assignments(booking_id, staff_id, shoot_date, shoot_time_slot ) VALUES
(1,2,'2026-07-01','08:00:00'),
(1,5,'2026-07-01','08:00:00'),
(2,3,'2026-07-01','14:00:00'),
(2,6,'2026-07-01','14:00:00'),
(3,4,'2026-07-02','08:00:00'),
(3,8,'2026-07-02','08:00:00'),
(4,2,'2026-07-03','09:00:00'),
(4,7,'2026-07-03','09:00:00');

-- =====================================================
-- BOOKING STATUS HISTORIES
-- =====================================================
INSERT INTO booking_status_histories(booking_id, previous_status, new_status, note, changed_by ) VALUES
(1,'PENDING','CONFIRMED','Khach da coc',1),
(2,'CONFIRMED','ASSIGNED', 'Da phan cong nhan su',1),
(3,'ASSIGNED','SHOOTING', 'Bat dau chup',1),
(4,'SHOOTING','EDITING', 'Da gui anh cho editor',1),
(5,'EDITING','COMPLETED', 'Ban giao anh',1);

-- =====================================================
-- POST PRODUCTION HISTORIES
-- =====================================================
INSERT INTO post_production_histories(booking_id, production_status, raw_photo_link, edited_photo_link, note, updated_by ) VALUES
(4, 'EDITING', 'https://drive.google.com/raw4', NULL, 'Dang chinh mau', 1 ),
(5, 'DELIVERED', 'https://drive.google.com/raw5', 'https://drive.google.com/final5', 'Da ban giao', 1 );

