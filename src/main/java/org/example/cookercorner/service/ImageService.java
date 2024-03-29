package org.example.cookercorner.service;

import org.example.cookercorner.entities.Image;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


public interface ImageService {

    Image saveImage(MultipartFile file);

     boolean isImageFile(MultipartFile file);

    void deleteUserImage(Long id);
}
