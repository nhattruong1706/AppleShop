package com.example.appleshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class FileUploadController {

    // 🔹 Đường dẫn thư mục lưu ảnh thật
    private static final String UPLOAD_DIR = "src/main/resources/static/img/";
    @PostMapping("/image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Giữ nguyên tên file, KHÔNG thêm timestamp
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            Path filePath = uploadPath.resolve(fileName);

            // ⚠️ Nếu đã tồn tại ảnh, KHÔNG ghi đè
            if (Files.exists(filePath)) {
                // chỉ trả về đường dẫn ảnh cũ, không copy lại
                String fileUrl = "/img/" + fileName;
                return ResponseEntity.ok(Map.of("url", fileUrl));
            }

            // Nếu chưa có thì mới copy
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/img/" + fileName;
            return ResponseEntity.ok(Map.of("url", fileUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Upload failed: " + e.getMessage()));
        }
    }

    }

