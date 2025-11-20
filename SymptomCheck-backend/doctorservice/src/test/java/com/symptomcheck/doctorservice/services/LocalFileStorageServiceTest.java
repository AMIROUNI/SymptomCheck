package com.symptomcheck.doctorservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

class LocalFileStorageServiceTest {

    @TempDir
    Path tempDir; // JUnit creates a real temporary directory

    private LocalFileStorageService service;

    @BeforeEach
    void setup() throws IOException {
        // Inject temp directory as uploadDir
        service = new LocalFileStorageService(tempDir.toString());
        service.init();
    }

    @Test
    void shouldStoreFileSuccessfully() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile("file", "test.png",
                        "image/png", "dummy content".getBytes());

        String storedPath = service.store(file);

        assertNotNull(storedPath);
        Path expectedFile = Paths.get(storedPath);

        assertTrue(Files.exists(expectedFile));
    }

    @Test
    void shouldReturnEmptyString_whenFileIsNull() throws Exception {
        String path = service.store(null);
        assertEquals("", path);
    }

    @Test
    void shouldThrowIOException_whenInvalidDirectory() {
        service = new LocalFileStorageService("//INVALID");

        assertThrows(InvalidPathException.class, service::init);
    }
}
