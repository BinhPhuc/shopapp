package com.project.shopapp.controllers;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.exception.NotFoundException;
import jakarta.validation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/products")
@Validated

public class ProductController {
    @GetMapping("")
    public ResponseEntity<String> getProducts(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        return ResponseEntity.ok("getProducts here!");
    }
    @PostMapping(value = "")
    public ResponseEntity<?> createProduct(@Valid @ModelAttribute ProductDTO productDTO) {
        try {
            List<MultipartFile> files = productDTO.getFiles();
            files = files == null ? new ArrayList<MultipartFile>() : files;
            for(MultipartFile file : files) {
                if(file.getSize() == 0) continue;
                if(file != null) {
                    if(file.getSize() > 10 * 1024 * 1024) {
                        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                                .body("File is too large! Maximum size is 10MB!");
                    }
                    String contentType = file.getContentType();
                    if(contentType == null || !contentType.startsWith("image/")) {
                        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("File must be an image!");
                    }
                    String fileName = uniqueFileName(file);
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body("Product created successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(e.getMessage());
        }
    }
    public String uniqueFileName(MultipartFile file) throws IOException {
        String folderName = "uploads";
        java.nio.file.Path storagePath = Paths.get(folderName);
        if(!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }
        String fileName = file.getOriginalFilename();
        String uniqueID = UUID.randomUUID().toString();
        String uniqueFileName = uniqueID + "_" + fileName;
        java.nio.file.Path destinationPath = Paths.get(folderName, uniqueFileName);
        Files.copy(file.getInputStream(), destinationPath);
        return uniqueFileName;
    }
    @GetMapping("/{user_id}")
    public ResponseEntity<String> getProductById(@PathVariable("user_id") String userId) {
        return ResponseEntity.ok("Product with ID: " + userId);
    }
    @DeleteMapping("/{user_id}")
    public ResponseEntity<String> deleteProductById(@PathVariable("user_id") long userId) {
        return ResponseEntity.ok(String.format("Product with id = %d delete successfully!", userId));
    }
}
