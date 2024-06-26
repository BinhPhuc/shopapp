package com.project.shopapp.services;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exception.InvalidParamException;
import com.project.shopapp.exception.NotFoundException;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.repositories.ProductImageRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.responses.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ModelMapper modelMapper;
    @Override
    public Product createProduct(ProductDTO productDTO) throws NotFoundException {
        Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found!"));
        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .category(existingCategory)
                .description(productDTO.getDescription())
                .build();
        newProduct.setCategory(existingCategory);
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(Long productId) throws NotFoundException {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Cannot find product with id = " + productId));
    }

    @Override
    public Page<ProductResponse> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest) {
        Page<Product> productsPage = productRepository.searchProduct(keyword, categoryId, pageRequest);
        return productsPage.map(ProductResponse::fromProduct);
    }

    @Override
    public Product updateProduct(Long id, ProductDTO productDTO) throws NotFoundException {
        Category existingCategory = categoryRepository.findById(productDTO.getCategoryId()).orElseThrow(() -> {
            return new NotFoundException("Category not found!");
        });
        Product currentProduct = getProductById(id);
        currentProduct.setName(productDTO.getName());
        currentProduct.setPrice(productDTO.getPrice());
        currentProduct.setThumbnail(productDTO.getThumbnail());
        currentProduct.setDescription(productDTO.getDescription());
        currentProduct.setCategory(existingCategory);
        return productRepository.save(currentProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        optionalProduct.ifPresent(productRepository::delete);
    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    public ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws NotFoundException, InvalidParamException {
        Product existingProduct = productRepository.findById(productId).orElseThrow(() -> {
            return new NotFoundException("Cannot find product with id = " + productImageDTO.getProductId());
        });
        ProductImage newProductImage = ProductImage
                .builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();
        // khong cho insert qua 5 anh
        int size = productImageRepository.findByProductId(productId).size();
        if(size >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            throw new InvalidParamException("You can only upload maximum 5 images!");
        }
        return productImageRepository.save(newProductImage);
    }

    @Override
    public List<ProductImage> getAllImageOfProduct(Long productId) {
        List<ProductImage> productImageList = productImageRepository.findByProductId(productId);
        return productImageList;
    }

    @Override
    public List<Product> getAllProductByIds(List<Long> ids) {
        List<Product> products = productRepository.findAllById(ids);
        return products;
    }
}
