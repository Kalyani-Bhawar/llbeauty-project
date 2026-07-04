package com.llbeauty.service;

import com.llbeauty.entity.ManualPaymentRequest;
import com.llbeauty.entity.User;
import com.llbeauty.repository.ManualPaymentRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ManualPaymentService {

    private final ManualPaymentRequestRepository manualPaymentRequestRepository;
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    public ManualPaymentService(ManualPaymentRequestRepository manualPaymentRequestRepository) {
        this.manualPaymentRequestRepository = manualPaymentRequestRepository;
    }

    @Transactional
    public ManualPaymentRequest submitManualPayment(User user, String paymentPurpose, Double amount, String utrNumber, String referenceId, MultipartFile screenshot) throws IOException {
        ManualPaymentRequest request = new ManualPaymentRequest();
        request.setUser(user);
        request.setPaymentPurpose(paymentPurpose);
        request.setAmount(amount);
        request.setUtrNumber(utrNumber);
        request.setReferenceId(referenceId);
        request.setStatus("PENDING");

        if (screenshot != null && !screenshot.isEmpty()) {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            String filename = UUID.randomUUID().toString() + "_" + screenshot.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, filename);
            Files.write(filePath, screenshot.getBytes());
            
            request.setScreenshotPath("/uploads/" + filename);
        }

        return manualPaymentRequestRepository.save(request);
    }
}
