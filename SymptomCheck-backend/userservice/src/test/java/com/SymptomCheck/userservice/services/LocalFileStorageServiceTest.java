package com.SymptomCheck.userservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class LocalFileStorageServiceTest {

    private LocalFileStorageService fileStorageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception { // <-- declare throws Exception
        fileStorageService = new LocalFileStorageService();

        // Use reflection to set private uploadDir
        Field uploadDirField = LocalFileStorageService.class.getDeclaredField("uploadDir");
        uploadDirField.setAccessible(true);
        uploadDirField.set(fileStorageService, tempDir.toString());

        fileStorageService.init();
    }

    @Test
    void testStoreFileSuccessfully() throws IOException {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Hello World".getBytes()
        );

        String storedPath = fileStorageService.store(mockFile);

        Path path = Path.of(storedPath);
        assertTrue(Files.exists(path));
        assertTrue(storedPath.endsWith("test.txt"));
        assertEquals("Hello World", Files.readString(path));
    }

    @Test
    void testStoreNullFileReturnsEmpty() throws IOException {
        String storedPath = fileStorageService.store(null);
        assertEquals("", storedPath);
    }

    @Test
    void testInitCreatesDirectory() throws Exception { // <-- throws Exception for reflection
        Path newDir = tempDir.resolve("newUploads");

        Field uploadDirField = LocalFileStorageService.class.getDeclaredField("uploadDir");
        uploadDirField.setAccessible(true);
        uploadDirField.set(fileStorageService, newDir.toString());

        fileStorageService.init();

        assertTrue(Files.exists(newDir));
        assertTrue(Files.isDirectory(newDir));
    }

}