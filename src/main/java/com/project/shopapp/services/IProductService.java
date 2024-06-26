package com.project.shopapp.services;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exception.InvalidParamException;
import com.project.shopapp.exception.NotFoundException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductService {
    Product createProduct(ProductDTO productDTO) throws NotFoundException;
    Product getProductById(Long id) throws NotFoundException;
    Page<ProductResponse> getAllProducts (String keyword, Long categoryId, PageRequest pageable);
    Product updateProduct(Long id, ProductDTO productDTO) throws NotFoundException;
    void deleteProduct(Long id);
    boolean existsByName(String name);

    ProductImage createProductImage(Long id, ProductImageDTO productImageDTO) throws NotFoundException, InvalidParamException;

    List<ProductImage> getAllImageOfProduct(Long productId);

    List<Product> getAllProductByIds(List<Long> ids);
}
