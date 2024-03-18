package org.example.cookercorner.repository;

import org.example.cookercorner.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}