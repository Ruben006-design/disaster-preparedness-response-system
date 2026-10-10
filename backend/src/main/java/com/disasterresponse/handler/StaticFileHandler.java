package com.disasterresponse.handler;

import com.disasterresponse.service.FileStorageService;
import com.disasterresponse.util.HttpUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.File;

public class StaticFileHandler extends BaseHandler {

    private final FileStorageService fileStorageService;

    public StaticFileHandler() {
        this.fileStorageService = new FileStorageService();
    }

    public StaticFileHandler(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Override
    protected void process(HttpExchange exchange) throws Exception {
        String path = exchange.getRequestURI().getPath();
        String fileName = null;

        if (path.startsWith("/uploads/")) {
            fileName = path.substring("/uploads/".length());
        } else if (path.startsWith("/api/evidence/view/")) {
            fileName = path.substring("/api/evidence/view/".length());
        }

        if (fileName == null || fileName.trim().isEmpty()) {
            sendError(exchange, 400, "File name must be specified.");
            return;
        }

        // Sanitize
        fileName = new File(fileName).getName();

        try {
            byte[] bytes = fileStorageService.loadFileBytes(fileName);
            String contentType = determineContentType(fileName);
            exchange.getResponseHeaders().set("Content-Disposition", "inline; filename=\"" + fileName + "\"");
            HttpUtil.sendByteResponse(exchange, 200, bytes, contentType);
        } catch (Exception e) {
            sendError(exchange, 404, "File not found: " + fileName);
        }
    }

    private String determineContentType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lower.endsWith(".png")) {
            return "image/png";
        } else if (lower.endsWith(".gif")) {
            return "image/gif";
        } else if (lower.endsWith(".webp")) {
            return "image/webp";
        } else if (lower.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (lower.endsWith(".mp4")) {
            return "video/mp4";
        } else if (lower.endsWith(".pdf")) {
            return "application/pdf";
        } else if (lower.endsWith(".mp3")) {
            return "audio/mpeg";
        }
        return "application/octet-stream";
    }
}
