package com.disasterresponse.util;

import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Pure Java multipart/form-data parser for handling file uploads without third-party frameworks.
 */
public class MultipartUtil {

    public static class FilePart {
        private final String fieldName;
        private final String fileName;
        private final String contentType;
        private final byte[] bytes;

        public FilePart(String fieldName, String fileName, String contentType, byte[] bytes) {
            this.fieldName = fieldName;
            this.fileName = fileName;
            this.contentType = contentType;
            this.bytes = bytes;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getFileName() {
            return fileName;
        }

        public String getContentType() {
            return contentType;
        }

        public byte[] getBytes() {
            return bytes;
        }
    }

    public static class MultipartData {
        private final Map<String, String> fields = new HashMap<>();
        private final List<FilePart> files = new ArrayList<>();

        public Map<String, String> getFields() {
            return fields;
        }

        public List<FilePart> getFiles() {
            return files;
        }

        public FilePart getFirstFile() {
            return files.isEmpty() ? null : files.get(0);
        }

        public String getFieldValue(String name) {
            return fields.get(name);
        }
    }

    public static MultipartData parse(HttpExchange exchange) throws IOException {
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (contentType == null || !contentType.toLowerCase().contains("multipart/form-data")) {
            throw new IllegalArgumentException("Request is not multipart/form-data");
        }

        String boundary = null;
        for (String param : contentType.split(";")) {
            param = param.trim();
            if (param.toLowerCase().startsWith("boundary=")) {
                boundary = param.substring(9).trim();
                if (boundary.startsWith("\"") && boundary.endsWith("\"")) {
                    boundary = boundary.substring(1, boundary.length() - 1);
                }
                break;
            }
        }

        if (boundary == null || boundary.isEmpty()) {
            throw new IllegalArgumentException("No boundary found in multipart request");
        }

        byte[] bodyBytes;
        try (InputStream is = exchange.getRequestBody()) {
            bodyBytes = is.readAllBytes();
        }

        MultipartData result = new MultipartData();
        byte[] boundaryBytes = ("--" + boundary).getBytes(StandardCharsets.ISO_8859_1);
        List<int[]> parts = findParts(bodyBytes, boundaryBytes);

        for (int[] partRange : parts) {
            int start = partRange[0];
            int end = partRange[1];
            if (start >= end) continue;

            // Find header delimiter \r\n\r\n or \n\n
            int headerEnd = findHeaderEnd(bodyBytes, start, end);
            if (headerEnd == -1) continue;

            String headerText = new String(bodyBytes, start, headerEnd - start, StandardCharsets.UTF_8);
            int dataStart = headerEnd;
            int dataEnd = end;

            // Strip trailing \r\n before the next boundary
            if (dataEnd >= dataStart + 2 && bodyBytes[dataEnd - 2] == '\r' && bodyBytes[dataEnd - 1] == '\n') {
                dataEnd -= 2;
            } else if (dataEnd >= dataStart + 1 && bodyBytes[dataEnd - 1] == '\n') {
                dataEnd -= 1;
            }

            Map<String, String> headers = parseHeaders(headerText);
            String disp = headers.get("content-disposition");
            if (disp == null) continue;

            String fieldName = extractParam(disp, "name");
            String fileName = extractParam(disp, "filename");
            String partContentType = headers.getOrDefault("content-type", "application/octet-stream");

            byte[] partData = Arrays.copyOfRange(bodyBytes, dataStart, dataEnd);

            if (fileName != null && !fileName.isEmpty()) {
                result.files.add(new FilePart(fieldName, fileName, partContentType, partData));
            } else if (fieldName != null) {
                result.fields.put(fieldName, new String(partData, StandardCharsets.UTF_8));
            }
        }

        return result;
    }

    private static List<int[]> findParts(byte[] data, byte[] boundary) {
        List<int[]> ranges = new ArrayList<>();
        List<Integer> positions = new ArrayList<>();

        for (int i = 0; i <= data.length - boundary.length; i++) {
            boolean match = true;
            for (int j = 0; j < boundary.length; j++) {
                if (data[i + j] != boundary[j]) {
                    match = false;
                    break;
                }
            }
            if (match) {
                positions.add(i);
                i += boundary.length - 1;
            }
        }

        for (int k = 0; k < positions.size() - 1; k++) {
            int partStart = positions.get(k) + boundary.length;
            // Skip CRLF immediately after boundary
            if (partStart + 1 < data.length && data[partStart] == '\r' && data[partStart + 1] == '\n') {
                partStart += 2;
            } else if (partStart < data.length && data[partStart] == '\n') {
                partStart += 1;
            }
            int partEnd = positions.get(k + 1);
            ranges.add(new int[]{partStart, partEnd});
        }

        return ranges;
    }

    private static int findHeaderEnd(byte[] data, int start, int end) {
        for (int i = start; i < end - 3; i++) {
            if (data[i] == '\r' && data[i + 1] == '\n' && data[i + 2] == '\r' && data[i + 3] == '\n') {
                return i + 4;
            }
        }
        for (int i = start; i < end - 1; i++) {
            if (data[i] == '\n' && data[i + 1] == '\n') {
                return i + 2;
            }
        }
        return -1;
    }

    private static Map<String, String> parseHeaders(String text) {
        Map<String, String> headers = new HashMap<>();
        String[] lines = text.split("\r?\n");
        for (String line : lines) {
            int colon = line.indexOf(':');
            if (colon > 0) {
                headers.put(line.substring(0, colon).trim().toLowerCase(), line.substring(colon + 1).trim());
            }
        }
        return headers;
    }

    private static String extractParam(String header, String param) {
        for (String item : header.split(";")) {
            item = item.trim();
            if (item.toLowerCase().startsWith(param.toLowerCase() + "=")) {
                String val = item.substring(param.length() + 1).trim();
                if (val.startsWith("\"") && val.endsWith("\"") && val.length() >= 2) {
                    val = val.substring(1, val.length() - 1);
                }
                return val;
            }
        }
        return null;
    }
}
