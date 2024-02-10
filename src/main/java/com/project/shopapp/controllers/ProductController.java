package com.project.shopapp.controllers;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exception.InvalidParamException;
import com.project.shopapp.exception.NotFoundException;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ProductListResponse;
import com.project.shopapp.responses.ProductResponse;
import com.project.shopapp.services.IProductService;
import com.project.shopapp.services.ProductService;
import com.project.shopapp.models.Product;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
@RequiredArgsConstructor

public class ProductController {
    private final IProductService productService;
    @GetMapping("")
    public ResponseEntity<ProductListResponse> getProducts(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        Page<ProductResponse> productPage = productService.getAllProducts(pageRequest);
        int totalPages = productPage.getTotalPages();
        List<ProductResponse> products = productPage.getContent();
        return ResponseEntity.ok(ProductListResponse
                .builder()
                .products(products)
                .totalPages(totalPages)
                .build());
    }
    @PostMapping(value = "")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO) throws NotFoundException {
        Product newProduct = productService.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.OK).body(newProduct);
    }
    @PostMapping(value = "/uploads/{id}")
    public ResponseEntity<?> uploadImages (@Valid @ModelAttribute List<MultipartFile> files,
                                           @PathVariable("id") Long productId)
            throws NotFoundException, InvalidParamException, IOException {
        Product existingProduct = productService.getProductById(productId);
        files = files == null ? new ArrayList<MultipartFile>() : files;
        if(files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            throw new InvalidParamException("You can only upload maximum 5 images!");
        }
        List<ProductImage> productImages = new ArrayList<>();
        for(MultipartFile file : files) {
            if(file.getSize() == 0) continue;
            if(file != null) {
                if(file.getSize() > 10 * 1024 * 1024) {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body("File is too large! Maximum size is 10MB!");
                }
                if(isImageFile(file)) {
                    String fileName = uniqueFileName(file);
                    ProductImage productImage = productService.createProductImage(productId, ProductImageDTO
                            .builder()
                            .imageUrl(fileName)
                            .build());
                    productImages.add(productImage);
                }

            }
        }
        return ResponseEntity.ok(productImages);
    }


    public String uniqueFileName(MultipartFile file) throws NotFoundException, IOException {
        if(!isImageFile(file) || file.getOriginalFilename() == null) {
            throw new IOException("Invalid format image file");
        }
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
    public boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        if(contentType == null || !contentType.startsWith("image/")) {
            return false;
        }
        return true;
    }

    @GetMapping("/{product_id}")
    public ResponseEntity<String> getProductById(@PathVariable("product_id") Long productId) {
        return ResponseEntity.ok("Product with ID: " + productId);
    }
    @DeleteMapping("/{product_id}")
    public ResponseEntity<String> deleteProductById(@PathVariable("product_id") Long productId) {
        return ResponseEntity.ok(String.format("Product with id = %d delete successfully!", productId));
    }
}
