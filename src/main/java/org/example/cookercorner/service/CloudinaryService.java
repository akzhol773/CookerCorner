package org.example.cookercorner.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


public interface CloudinaryService {
    public String uploadFile(MultipartFile file, String folderName);
}
