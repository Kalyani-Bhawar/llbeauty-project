package com.llbeauty.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class UploadController {

    @Value("${app.upload.root}")
    private String uploadRoot;

    @PostMapping("/api/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }

        try {
            String originalFileName = file.getOriginalFilename();
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + extension;

            // Define upload subdirectory inside static resources
            String subDir = "documents";
            
            // Save to src directory (for persistence)
            Path srcUploadPath = Paths.get(uploadRoot, "src/main/resources/static/uploads", subDir);
            if (!Files.exists(srcUploadPath)) {
                Files.createDirectories(srcUploadPath);
            }
            Path srcFilePath = srcUploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), srcFilePath);

            // Save to target directory (for immediate web serving)
            Path targetUploadPath = Paths.get(uploadRoot, "target/classes/static/uploads", subDir);
            if (!Files.exists(targetUploadPath)) {
                Files.createDirectories(targetUploadPath);
            }
            Path targetFilePath = targetUploadPath.resolve(fileName);
            // Copy file again to target directory
            Files.copy(srcFilePath, targetFilePath);

            String fileUrl = "/uploads/" + subDir + "/" + fileName;
            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to store file: " + e.getMessage()));
        }
    }
}
