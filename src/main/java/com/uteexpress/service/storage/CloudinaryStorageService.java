package com.uteexpress.service.storage;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryStorageService {
    String uploadFile(MultipartFile file);
}
