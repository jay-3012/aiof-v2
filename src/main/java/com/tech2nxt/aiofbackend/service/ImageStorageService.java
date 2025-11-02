package com.tech2nxt.aiofbackend.service;

import com.tech2nxt.aiofbackend.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ImageStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );
    private static final int MAX_WIDTH = 800;
    private static final int MAX_HEIGHT = 800;

    /**
     * Save and compress uploaded image
     */
    public String saveImage(MultipartFile file, Long userId) {
        log.info("Saving image for user ID: {}", userId);

        // Validate file
        validateImage(file);

        try {
            // Create upload directory if not exists
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Created upload directory: {}", uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String filename = String.format("%d_%d_%s.%s",
                    userId,
                    System.currentTimeMillis(),
                    UUID.randomUUID().toString().substring(0, 8),
                    extension);

            Path filePath = uploadPath.resolve(filename);

            // Compress and save image
            if (needsCompression(file)) {
                log.info("Compressing image: {}", filename);
                Thumbnails.of(file.getInputStream())
                        .size(MAX_WIDTH, MAX_HEIGHT)
                        .outputQuality(0.85)
                        .toFile(filePath.toFile());
            } else {
                // Save without compression for small images
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            log.info("Image saved successfully: {}", filename);
            return filename;

        } catch (IOException e) {
            log.error("Failed to save image", e);
            throw new RuntimeException("Failed to save image: " + e.getMessage());
        }
    }

    /**
     * Delete image file
     */
    public void deleteImage(String filename) {
        if (filename == null || filename.isEmpty()) {
            return;
        }

        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            Files.deleteIfExists(filePath);
            log.info("Image deleted: {}", filename);
        } catch (IOException e) {
            log.error("Failed to delete image: {}", filename, e);
        }
    }

    /**
     * Validate uploaded image
     */
    private void validateImage(MultipartFile file) {
        // Check if file is empty
        if (file.isEmpty()) {
            throw new BadRequestException("Please select an image to upload");
        }

        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size must not exceed 5MB");
        }

        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Only JPG, PNG, and WebP images are allowed");
        }

        // Check file extension
        String filename = file.getOriginalFilename();
        if (filename == null || !hasValidExtension(filename)) {
            throw new BadRequestException("Invalid file extension");
        }
    }

    /**
     * Check if file extension is valid
     */
    private boolean hasValidExtension(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return Arrays.asList("jpg", "jpeg", "png", "webp").contains(extension);
    }

    /**
     * Get file extension
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * Check if image needs compression
     */
    private boolean needsCompression(MultipartFile file) {
        // Compress if file is larger than 500KB
        return file.getSize() > 500 * 1024;
    }

    /**
     * Get upload directory path
     */
    public String getUploadDirectory() {
        return uploadDir;
    }
}