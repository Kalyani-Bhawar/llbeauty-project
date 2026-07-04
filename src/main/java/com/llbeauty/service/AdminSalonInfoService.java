package com.llbeauty.service;

import com.llbeauty.entity.SalonInfo;
import com.llbeauty.repository.SalonInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class AdminSalonInfoService {

    private final SalonInfoRepository salonInfoRepository;
    
    @Value("${app.upload.root}")
    private String projectRoot;

    public AdminSalonInfoService(SalonInfoRepository salonInfoRepository) {
        this.salonInfoRepository = salonInfoRepository;
    }

    private static final java.util.List<String> ALLOWED_IMAGE_TYPES = java.util.Arrays.asList("image/jpeg", "image/png", "image/webp", "image/gif");

    public String saveUploadedFile(MultipartFile file, String subDir) throws IOException {
        if (file.isEmpty()) return null;

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new IOException("Only image files (JPEG, PNG, WEBP, GIF) are allowed!");
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replaceAll("\\s+", "_");
        String root = (this.projectRoot != null && !this.projectRoot.isEmpty()) ? this.projectRoot : ".";
        byte[] fileBytes = file.getBytes();

        Path srcUploadPath = Paths.get(root, "src/main/resources/static/uploads", subDir);
        if (!Files.exists(srcUploadPath)) {
            Files.createDirectories(srcUploadPath);
        }
        Path srcFilePath = srcUploadPath.resolve(fileName);
        Files.write(srcFilePath, fileBytes);

        Path targetUploadPath = Paths.get(root, "target/classes/static/uploads", subDir);
        if (!Files.exists(targetUploadPath)) {
            Files.createDirectories(targetUploadPath);
        }
        Path targetFilePath = targetUploadPath.resolve(fileName);
        Files.write(targetFilePath, fileBytes);

        return "/uploads/" + subDir + "/" + fileName;
    }

    @Transactional
    public void saveSalonInfo(SalonInfo salonInfo, MultipartFile imageFile) throws IOException {
        Optional<SalonInfo> infoOpt = salonInfoRepository.findById(1L);
        SalonInfo flagship = infoOpt.orElse(salonInfo);
        
        flagship.setId(1L);
        flagship.setName(salonInfo.getName());
        flagship.setTagline(salonInfo.getTagline());
        flagship.setDescription(salonInfo.getDescription());
        flagship.setAddress(salonInfo.getAddress());
        flagship.setContactPhone(salonInfo.getContactPhone());
        flagship.setContactEmail(salonInfo.getContactEmail());
        flagship.setTimings(salonInfo.getTimings());

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = saveUploadedFile(imageFile, "salon");
            flagship.setImageUrl(imageUrl);
        }
        salonInfoRepository.save(flagship);
    }
}
