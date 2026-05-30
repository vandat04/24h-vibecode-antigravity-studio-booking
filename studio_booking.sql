DROP DATABASE IF EXISTS studio_booking;

CREATE DATABASE studio_booking
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE studio_booking;

-- =====================================================
-- ROLES
-- =====================================================
CREATE TABLE roles (
id TINYINT UNSIGNED AUTO_INCREMENT,
role_name VARCHAR(20) NOT NULL,
PRIMARY KEY (id),
UNIQUE KEY uk_role_name (role_name)
) ENGINE=InnoDB;

INSERT INTO roles(role_name)
VALUES
('ADMIN'),
('MAKEUP'),
('PHOTOGRAPHER');

-- =====================================================
-- USERS
-- =====================================================
CREATE TABLE users (
id INT UNSIGNED AUTO_INCREMENT,
role_id TINYINT UNSIGNED NOT NULL,
username VARCHAR(50) NOT NULL,
password_hash VARCHAR(255) NOT NULL,
full_name VARCHAR(100) NOT NULL,
email VARCHAR(100) NOT NULL,
phone VARCHAR(15),
is_active BOOLEAN NOT NULL DEFAULT TRUE,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (id),
UNIQUE KEY uk_username(username),
UNIQUE KEY uk_email(email),
CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id) ) ENGINE=InnoDB;

-- =====================================================
-- STAFF PROFILES
-- =====================================================
CREATE TABLE staff_profiles (
id INT UNSIGNED AUTO_INCREMENT,
user_id INT UNSIGNED NOT NULL,
avatar_url VARCHAR(500) NOT NULL,
bio TEXT,
experience_detail TEXT,
years_of_experience TINYINT UNSIGNED NOT NULL DEFAULT 0,
facebook_url VARCHAR(255),
instagram_url VARCHAR(255),
tiktok_url VARCHAR(255),
is_displayed BOOLEAN NOT NULL DEFAULT TRUE,
PRIMARY KEY (id),
UNIQUE KEY uk_user_profile(user_id),
CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ) ENGINE=InnoDB;

-- =====================================================
-- STUDIO INFORMATION
-- =====================================================
CREATE TABLE studio_information (
id TINYINT UNSIGNED AUTO_INCREMENT,
studio_name VARCHAR(150) NOT NULL,
logo_url VARCHAR(500) NOT NULL,
banner_url VARCHAR(500) NOT NULL,
address VARCHAR(255) NOT NULL,
phone VARCHAR(15) NOT NULL,
email VARCHAR(100) NOT NULL,
facebook_url VARCHAR(255),
zalo_url VARCHAR(255),
youtube_url VARCHAR(255),
intro_video_url VARCHAR(255),
introduction TEXT NOT NULL,
working_process TEXT NOT NULL,
google_map_url TEXT NOT NULL,
PRIMARY KEY (id) ) ENGINE=InnoDB;

-- =====================================================
-- CONCEPTS
-- =====================================================
CREATE TABLE concepts (
id INT UNSIGNED AUTO_INCREMENT,
title VARCHAR(150) NOT NULL,
slug VARCHAR(150) NOT NULL,
concept_type ENUM( 'BEAUTY', 'COUPLE', 'BIRTHDAY', 'FAMILY', 'OUTDOOR', 'EVENT', 'OTHER') NOT NULL,
thumbnail_url VARCHAR(500) NOT NULL,
description TEXT,
status ENUM( 'DRAFT', 'PUBLISHED') NOT NULL DEFAULT 'PUBLISHED',
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (id),
UNIQUE KEY uk_concept_slug(slug) ) ENGINE=InnoDB;

-- =====================================================
-- CONCEPT IMAGES
-- =====================================================
CREATE TABLE concept_images (
id BIGINT UNSIGNED AUTO_INCREMENT,
concept_id INT UNSIGNED NOT NULL,
image_url VARCHAR(500) NOT NULL,
sort_order SMALLINT NOT NULL DEFAULT 0,
PRIMARY KEY (id),
CONSTRAINT fk_images_concept FOREIGN KEY (concept_id) REFERENCES concepts(id) ON DELETE CASCADE) ENGINE=InnoDB;

-- =====================================================
-- SERVICE PACKAGES
-- =====================================================
CREATE TABLE service_packages (
id INT UNSIGNED AUTO_INCREMENT,
package_name VARCHAR(150) NOT NULL,
slug VARCHAR(150) NOT NULL,
price DECIMAL(12,2) NOT NULL,
short_description VARCHAR(500),
detail_content TEXT NOT NULL,
layout_count TINYINT UNSIGNED NOT NULL DEFAULT 1,
outfit_count TINYINT UNSIGNED NOT NULL DEFAULT 1,
edited_photos TINYINT UNSIGNED NOT NULL DEFAULT 0,
makeup_person_count TINYINT UNSIGNED NOT NULL DEFAULT 0,
thumbnail_url VARCHAR(500) NOT NULL,
is_active BOOLEAN NOT NULL DEFAULT TRUE,
PRIMARY KEY (id),
UNIQUE KEY uk_package_slug(slug) ) ENGINE=InnoDB;

-- =====================================================
-- BLOGS
-- =====================================================
CREATE TABLE blogs (
id INT UNSIGNED AUTO_INCREMENT,
title VARCHAR(200) NOT NULL,
slug VARCHAR(200) NOT NULL,
thumbnail_url VARCHAR(500) NOT NULL,
content LONGTEXT NOT NULL,
related_concept_id INT UNSIGNED,
status ENUM( 'DRAFT', 'PUBLISHED') NOT NULL DEFAULT 'DRAFT',
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (id),
UNIQUE KEY uk_blog_slug(slug),
CONSTRAINT fk_blogs_concept FOREIGN KEY (related_concept_id) REFERENCES concepts(id) ON DELETE SET NULL ) ENGINE=InnoDB;

-- =====================================================
-- CUSTOMER STORIES
-- =====================================================
CREATE TABLE customer_stories (
id INT UNSIGNED AUTO_INCREMENT,
customer_name VARCHAR(100) NOT NULL,
avatar_url VARCHAR(500),
story_content TEXT NOT NULL,
is_displayed BOOLEAN NOT NULL DEFAULT TRUE,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (id) ) ENGINE=InnoDB;

-- =====================================================
-- BOOKINGS
-- =====================================================
CREATE TABLE bookings (
id INT UNSIGNED AUTO_INCREMENT,
booking_code VARCHAR(20) NOT NULL,
customer_name VARCHAR(100) NOT NULL,
customer_email VARCHAR(100) NOT NULL,
customer_phone VARCHAR(15) NOT NULL,
customer_facebook VARCHAR(255),
shoot_date DATE NOT NULL,
shoot_time_slot TIME NOT NULL,
shoot_location VARCHAR(255) NOT NULL,
package_id INT UNSIGNED NOT NULL,
concept_id INT UNSIGNED NOT NULL,
customer_notes TEXT,
total_amount DECIMAL(12,2) NOT NULL,
booking_status ENUM( 'PENDING', 'CONFIRMED', 'ASSIGNED', 'SHOOTING', 'EDITING', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
payment_status ENUM( 'UNPAID', 'DEPOSITED', 'PAID', 'REFUNDED') NOT NULL DEFAULT 'UNPAID',
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (id),
UNIQUE KEY uk_booking_code(booking_code),
CONSTRAINT fk_bookings_package FOREIGN KEY (package_id) REFERENCES service_packages(id),
CONSTRAINT fk_bookings_concept FOREIGN KEY (concept_id) REFERENCES concepts(id) ) ENGINE=InnoDB;

-- =====================================================
-- BOOKING ASSIGNMENTS
-- =====================================================
CREATE TABLE booking_assignments (
id INT UNSIGNED AUTO_INCREMENT,
booking_id INT UNSIGNED NOT NULL,
staff_id INT UNSIGNED NOT NULL,
shoot_date DATE NOT NULL,
shoot_time_slot TIME NOT NULL,
assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (id),
UNIQUE KEY uk_staff_schedule ( staff_id, shoot_date, shoot_time_slot),
CONSTRAINT fk_assignments_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
CONSTRAINT fk_assignments_staff FOREIGN KEY (staff_id) REFERENCES users(id) ) ENGINE=InnoDB;

-- =====================================================
-- BOOKING STATUS HISTORIES
-- =====================================================
CREATE TABLE booking_status_histories (
id BIGINT UNSIGNED AUTO_INCREMENT,
booking_id INT UNSIGNED NOT NULL,
previous_status VARCHAR(30),
new_status VARCHAR(30) NOT NULL,
note VARCHAR(255),
changed_by INT UNSIGNED NOT NULL,
changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (id),
CONSTRAINT fk_histories_booking  FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
CONSTRAINT fk_histories_user FOREIGN KEY (changed_by) REFERENCES users(id) ) ENGINE=InnoDB;

-- =====================================================
-- POST PRODUCTION HISTORIES
-- =====================================================
CREATE TABLE post_production_histories (
id BIGINT UNSIGNED AUTO_INCREMENT,
booking_id INT UNSIGNED NOT NULL,
production_status ENUM( 'UNPROCESSED', 'EDITING', 'WAITING_APPROVAL', 'DELIVERED') NOT NULL DEFAULT 'UNPROCESSED',
raw_photo_link VARCHAR(500),
edited_photo_link VARCHAR(500),
note VARCHAR(255),
updated_by INT UNSIGNED NOT NULL,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (id),
CONSTRAINT fk_production_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
CONSTRAINT fk_production_user FOREIGN KEY (updated_by) REFERENCES users(id) ) ENGINE=InnoDB;

-- =====================================================
-- BOOKING HOLDS (TEMPORARY SLOT LOCKS)
-- =====================================================
CREATE TABLE booking_holds (
  id BIGINT UNSIGNED AUTO_INCREMENT,
  shoot_date DATE NOT NULL,
  shoot_time_slot TIME NOT NULL,
  hold_expired_at TIMESTAMP NOT NULL,
  hold_token VARCHAR(50) NOT NULL,
  concept_id INT UNSIGNED,
  package_id INT UNSIGNED,
  PRIMARY KEY (id),
  UNIQUE KEY uk_hold_token (hold_token),
  CONSTRAINT fk_holds_concept FOREIGN KEY (concept_id) REFERENCES concepts(id) ON DELETE SET NULL,
  CONSTRAINT fk_holds_package FOREIGN KEY (package_id) REFERENCES service_packages(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- =====================================================
-- INDEXES
-- =====================================================
CREATE INDEX idx_bookings_search ON bookings(customer_phone, booking_code);
CREATE INDEX idx_bookings_schedule ON bookings(shoot_date, shoot_time_slot);
CREATE INDEX idx_concepts_lookup ON concepts(slug, status);
CREATE INDEX idx_packages_lookup ON service_packages(slug, is_active);
CREATE INDEX idx_booking_holds_expired ON booking_holds(hold_expired_at);
CREATE INDEX idx_booking_holds_slot ON booking_holds(shoot_date, shoot_time_slot);
CREATE INDEX idx_booking_holds_expired ON booking_holds(hold_expired_at);
CREATE INDEX idx_booking_holds_slot ON booking_holds(shoot_date, shoot_time_slot);
