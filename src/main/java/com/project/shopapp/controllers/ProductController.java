package com.project.shopapp.controllers;

import com.github.javafaker.Faker;
import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exception.InvalidParamException;
import com.project.shopapp.exception.NotFoundException;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ProductListResponse;
import com.project.shopapp.responses.ProductResponse;
import com.project.shopapp.services.IProductService;
import com.project.shopapp.models.Product;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
            @RequestParam(defaultValue = "", name = "keyword") String keyword,
            @RequestParam(defaultValue = "0", name = "categoryId") Long categoryId,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "limit") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(
                page,
                limit,
                Sort.by("id"));
        Page<ProductResponse> productPage = productService.getAllProducts(keyword, categoryId, pageRequest);
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
        return ResponseEntity.ok(ProductResponse.fromProduct(newProduct));
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

    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage (@PathVariable("imageName") String imageName) {
        try {
            String fileName = "uploads/" + imageName;
            java.nio.file.Path imagePath = Paths.get(fileName);
            Resource resource = new FileSystemResource(imagePath);
            if(resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                String errorImageName = "uploads/404_not_found.png";
                java.nio.file.Path image_404_path = Paths.get(errorImageName);
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new FileSystemResource(image_404_path));
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
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

    @GetMapping("/view/images/{product_id}")
    public ResponseEntity<?> getProductImagesByProductId(@PathVariable("product_id") Long productId) {
        List<ProductImage> productImagesList = productService.getAllImageOfProduct(productId);
        return ResponseEntity.ok(productImagesList);
    }

    @GetMapping("/{product_id}")
    public ResponseEntity<?> getProductById(@PathVariable("product_id") Long productId) throws NotFoundException {
        Product existingProduct = productService.getProductById(productId);
        return ResponseEntity.ok(ProductResponse.fromProduct(existingProduct));
    }

    @GetMapping("/by-ids")
    public ResponseEntity<?> getAllProductsByIds(@RequestParam("ids") List<Long> ids) {
        List<Product> products = productService.getAllProductByIds(ids);
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/{product_id}")
    @Transactional
    public ResponseEntity<String> deleteProductById(@PathVariable("product_id") Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok(String.format("Product with id = %d delete successfully!", productId));
    }

    @PutMapping("/{product_id}")
    @Transactional
    public ResponseEntity<?> updateProductById(@PathVariable("product_id") Long productId,
                                                   @Valid @RequestBody ProductDTO productDTO) throws NotFoundException {
        productService.updateProduct(productId, productDTO);
        Product productAfterUpdated = productService.getProductById(productId);
        return ResponseEntity.ok(ProductResponse.fromProduct(productAfterUpdated));
    }

    // fake products
    @PostMapping("/generateFakeProducts")
    private ResponseEntity<String> generateFakeProducts () throws NotFoundException {
        Faker faker = new Faker();
        for(int i = 1; i <= 1000; i++) {
            String productName = faker.commerce().productName();
            if(productService.existsByName(productName)) continue;
            ProductDTO productDTO = ProductDTO
                    .builder()
                    .name(productName)
                    .price((float)faker.number().randomDouble(4, 10, 10000))
                    .thumbnail("")
                    .description(faker.lorem().sentence())
                    .categoryId((long)faker.number().numberBetween(8, 12))
                    .build();
            productService.createProduct(productDTO);
        }
        return ResponseEntity.ok("Fake products created successfully!");
    }
}
