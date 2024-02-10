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

public interface IProductService {
    Product createProduct(ProductDTO productDTO) throws NotFoundException;
    Product getProductById(Long id) throws NotFoundException;
    Page<ProductResponse> getAllProducts (PageRequest pageRequest);
    Product updateProduct(Long id, ProductDTO productDTO) throws NotFoundException;
    void deleteProduct(Long id);
    boolean existsByName(String name);

    ProductImage createProductImage(Long id, ProductImageDTO productImageDTO) throws NotFoundException, InvalidParamException;
}
