package com.uteexpress.service.impl;

import com.cloudinary.Cloudinary;
import com.uteexpress.service.storage.CloudinaryStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;


@Service
public class CloudinaryStorageServiceImpl implements CloudinaryStorageService {

    private final Cloudinary cloudinary;

    public CloudinaryStorageServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> upload = (Map<String, Object>) cloudinary.uploader()
                    .upload(file.getBytes(), Map.of());
            return upload.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi upload Cloudinary: " + e.getMessage());
        }
    }
}

