package com.studio.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.studio.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        // 1. Kiểm tra tập tin trống
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Tệp tin tải lên không được để trống.");
        }

        // 2. Kiểm tra định dạng tệp tin phải là hình ảnh
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Chỉ cho phép tải lên các tệp tin hình ảnh!");
        }

        try {
            // 3. Cấu hình tham số tải ảnh lên Cloudinary
            Map params = ObjectUtils.asMap(
                    "folder", folder,
                    "use_filename", true,
                    "unique_filename", true,
                    "resource_type", "image"
            );

            // 4. Upload tệp tin nhị phân và trích xuất URL an toàn (secure_url)
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
            String secureUrl = (String) uploadResult.get("secure_url");
            
            log.info("Tải ảnh thành công lên Cloudinary. URL: {}", secureUrl);
            return secureUrl;

        } catch (IOException e) {
            log.error("Lỗi xảy ra trong quá trình truyền tải tệp tin nhị phân lên Cloudinary: {}", e.getMessage());
            throw new RuntimeException("Không thể truyền tải tệp tin lên Cloudinary. Vui lòng thử lại sau.");
        }
    }
}
