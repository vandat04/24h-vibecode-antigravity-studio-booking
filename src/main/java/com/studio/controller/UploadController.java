package com.studio.controller;

import com.studio.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class UploadController {

    private final CloudinaryService cloudinaryService;

    /**
     * POST /api/admin/upload
     * Tiếp nhận tập tin hình ảnh từ Client, upload lên đám mây Cloudinary và trả về URL CDN an toàn.
     * Rất tiện lợi cho React Frontend tái sử dụng để tải lên avatar, ảnh bối cảnh, ảnh bìa, v.v.
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "studio") String folder) {
        
        String secureUrl = cloudinaryService.uploadFile(file, folder);
        
        return ResponseEntity.ok(Map.of(
                "url", secureUrl,
                "message", "Tải ảnh lên đám mây Cloudinary thành công!"
        ));
    }
}
