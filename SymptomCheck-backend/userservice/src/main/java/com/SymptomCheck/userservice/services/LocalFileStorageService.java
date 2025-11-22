    package com.SymptomCheck.userservice.services;

    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Service;
    import org.springframework.web.multipart.MultipartFile;

    import jakarta.annotation.PostConstruct;
    import java.io.IOException;
    import java.nio.file.*;

    @Service
    public class LocalFileStorageService {
        @Value("${app.upload-dir:uploads}")
        private String uploadDir;

        @PostConstruct
        public void init() throws IOException {
            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) Files.createDirectories(path);
        }

        public String store(MultipartFile file) throws IOException {
            if(file != null){
            String filename = System.currentTimeMillis() + "_" + Path.of(file.getOriginalFilename()).getFileName();
            Path target = Paths.get(uploadDir).resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return target.toString();
            }
            return "";
        }
    }