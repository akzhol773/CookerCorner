package org.example.cookercorner.service.Impl;

import jakarta.transaction.Transactional;
import org.example.cookercorner.entities.Image;
import org.example.cookercorner.repository.ImageRepository;
import org.example.cookercorner.service.CloudinaryService;
import org.example.cookercorner.service.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;
    private final CloudinaryService cloudinaryService;

    public ImageServiceImpl(ImageRepository imageRepository, CloudinaryService cloudinaryService) {
        this.imageRepository = imageRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    @Transactional
    public Image saveImage(MultipartFile file) {
        Image image = new Image();
        try {

            image.setUrl(cloudinaryService.uploadFile(file, "Recipes picture"));
            imageRepository.save(image);
        }catch (Exception exception){
            throw new RuntimeException("Image upload failed: " + exception.getMessage());
        }

        return image;

    }

    @Override
    public void deleteUserImage(Long photoId) {
        imageRepository.deleteById(photoId);
    }


}
