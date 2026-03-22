package com.syncride.userservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    private Cloudinary cloudinary;

    @PostConstruct
    public void init() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    /**
     * Uploads a file to Cloudinary, matching the Node.js behavior:
     * - Folder: syncride_v1
     * - public_id: {timestamp}_{originalFilename}
     * - resource_type: "image" for images, "raw" otherwise
     */
    public String uploadFile(MultipartFile file) throws IOException {

        String contentType = file.getContentType();
        String resourceType = (contentType != null && contentType.startsWith("image"))
                ? "image" : "raw";

        String publicId = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "syncride_v1",
                "resource_type", resourceType,
                "public_id", publicId
        ));

        return (String) result.get("secure_url");
    }
}
