package com.Esteban.cinema.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String subirImagen(MultipartFile file) {
        return subirImagen(file, null);
    }

    public String subirImagen(MultipartFile file, String publicId) {
        try {
            Map<String, Object> options = ObjectUtils.asMap(
                    "resource_type", "image",
                    "overwrite", true,
                    "secure", true
            );
            if (publicId != null && !publicId.isBlank()) {
                options.put("public_id", publicId);
            }
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Error uploading image to Cloudinary: " + e.getMessage(), e);
        }
    }
}
