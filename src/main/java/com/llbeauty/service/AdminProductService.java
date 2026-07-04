package com.llbeauty.service;

import com.llbeauty.entity.Product;
import com.llbeauty.repository.ProductRepository;
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
public class AdminProductService {

    private final ProductRepository productRepository;
    
    @Value("${app.upload.root}")
    private String projectRoot;

    public AdminProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
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
    public void saveProduct(Product product, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = saveUploadedFile(imageFile, "products");
            product.setImageUrl(imageUrl);
        } else if (product.getImageUrl() == null || product.getImageUrl().isEmpty()) {
            product.setImageUrl("/images/skincare.png");
        }

        if (product.getMerchantDiscount() == null) {
            product.setMerchantDiscount(0.0);
        }

        if (product.getWholesalePrice() == null) {
            product.setWholesalePrice(0.0);
        }

        if (product.getStatus() == null || product.getStatus().isBlank()) {
            product.setStatus("ACTIVE");
        }
        if (product.getPrice() != null && product.getMerchantDiscount() != null) {
            double wholesalePrice = product.getPrice() - (product.getPrice() * product.getMerchantDiscount() / 100);
            product.setWholesalePrice(wholesalePrice);
        }

        productRepository.save(product);
    }

    @Transactional
    public void updateProduct(Long id, Product productDetails, MultipartFile imageFile) throws IOException {
        Optional<Product> prodOpt = productRepository.findById(id);
        if (prodOpt.isPresent()) {
            Product product = prodOpt.get();
            product.setName(productDetails.getName());
            product.setCategory(productDetails.getCategory());
            product.setDescription(productDetails.getDescription());
            product.setPrice(productDetails.getPrice());
            product.setStock(productDetails.getStock());
            product.setStatus(productDetails.getStatus());
            product.setMerchantDiscount(productDetails.getMerchantDiscount());
            
            if (product.getPrice() != null && product.getMerchantDiscount() != null) {
                double wholesalePrice = product.getPrice() - (product.getPrice() * product.getMerchantDiscount() / 100);
                product.setWholesalePrice(wholesalePrice);
            }

            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = saveUploadedFile(imageFile, "products");
                product.setImageUrl(imageUrl);
            }
            productRepository.save(product);
        } else {
            throw new IllegalArgumentException("Product not found");
        }
    }
}
