package com.symptomcheck.doctorservice.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class LocalFileStorageService {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    // ✅ REMOVE this constructor - it's causing the injection problem
    // @Autowired(required = false)
    // public LocalFileStorageService(String uploadDir) {
    //     this.uploadDir = uploadDir;
    // }

    @PostConstruct
    public void init() throws IOException {
        Path path = Paths.get(uploadDir);
        if (!Files.exists(path)) Files.createDirectories(path);
    }

    public String store(MultipartFile file) throws IOException {
        if (file == null) {
            return "";
        }

        try {
            // ✅ FIX: Add .toString() to getFileName()
            String filename = System.currentTimeMillis() + "_" +
                    Path.of(file.getOriginalFilename()).getFileName().toString();

            Path target = Paths.get(uploadDir).resolve(filename);

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return target.toString();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Failed to store file", e);
        }
    }
}