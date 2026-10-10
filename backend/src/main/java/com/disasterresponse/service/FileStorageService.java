package com.disasterresponse.service;

import com.disasterresponse.config.DatabaseConfig;
import com.disasterresponse.util.MultipartUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * File storage service that stores disaster evidence files on local disk (./uploads).
 * Returns web-accessible URL paths for frontend display.
 */
public class FileStorageService {

    private final Path uploadPath;

    public FileStorageService() {
        String uploadDir = DatabaseConfig.getUploadDir();
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + this.uploadPath, e);
        }
    }

    public StoredFile storeFile(MultipartUtil.FilePart filePart) {
        String originalFileName = filePart.getFileName();
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            originalFileName = "unnamed_file";
        }

        // Clean filename
        String cleanName = new File(originalFileName).getName();
        String ext = "";
        int dotIdx = cleanName.lastIndexOf('.');
        if (dotIdx > 0) {
            ext = cleanName.substring(dotIdx);
        }

        String storedFileName = UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + ext;
        Path targetLocation = this.uploadPath.resolve(storedFileName);

        try (FileOutputStream fos = new FileOutputStream(targetLocation.toFile())) {
            fos.write(filePart.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + originalFileName, e);
        }

        String fileUrl = "/api/evidence/view/" + storedFileName;
        return new StoredFile(originalFileName, storedFileName, filePart.getContentType(), (long) filePart.getBytes().length, fileUrl);
    }

    public byte[] loadFileBytes(String fileName) throws IOException {
        Path filePath = this.uploadPath.resolve(fileName).normalize();
        if (!Files.exists(filePath) || !filePath.startsWith(this.uploadPath)) {
            throw new RuntimeException("File not found: " + fileName);
        }
        return Files.readAllBytes(filePath);
    }

    public static class StoredFile {
        private final String originalFileName;
        private final String storedFileName;
        private final String fileType;
        private final Long fileSize;
        private final String fileUrl;

        public StoredFile(String originalFileName, String storedFileName, String fileType, Long fileSize, String fileUrl) {
            this.originalFileName = originalFileName;
            this.storedFileName = storedFileName;
            this.fileType = fileType;
            this.fileSize = fileSize;
            this.fileUrl = fileUrl;
        }

        public String getOriginalFileName() {
            return originalFileName;
        }

        public String getStoredFileName() {
            return storedFileName;
        }

        public String getFileType() {
            return fileType;
        }

        public Long getFileSize() {
            return fileSize;
        }

        public String getFileUrl() {
            return fileUrl;
        }
    }
}
