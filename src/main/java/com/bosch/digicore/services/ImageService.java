package com.bosch.digicore.services;

import com.bosch.digicore.entities.Image;
import com.bosch.digicore.exceptions.BadRequestException;
import com.bosch.digicore.exceptions.ResourceNotFoundException;
import com.bosch.digicore.repositories.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    @Transactional(readOnly = true)
    public Image getById(String id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Image.class.getName(), "id", id));
    }

    public Image save(MultipartFile file) {
        String name = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            Image image = new Image(name, file.getContentType(), file.getBytes());
            return imageRepository.save(image);
        } catch (IOException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException("Could not store image " + name + ". Please try again!");
        }
    }

    public void deleteById(String id) {
        imageRepository.deleteById(id);
    }
}
