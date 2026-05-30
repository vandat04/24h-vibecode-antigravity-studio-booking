package com.studio.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    /**
     * Tải tệp tin hình ảnh lên đám mây Cloudinary và nhận về đường dẫn URL an toàn.
     * 
     * @param file Tệp tin hình ảnh gửi từ client
     * @param folder Thư mục lưu trữ trên Cloudinary (e.g. "avatar", "concept")
     * @return Chuỗi URL an toàn (secure_url)
     */
    String uploadFile(MultipartFile file, String folder);
}
