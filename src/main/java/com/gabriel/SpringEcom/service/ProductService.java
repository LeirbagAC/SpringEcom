package com.gabriel.SpringEcom.service;

import com.gabriel.SpringEcom.model.Product;
import com.gabriel.SpringEcom.model.ProductImage;
import com.gabriel.SpringEcom.repo.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepo productRepo;

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Product getProductById(int id) {
        return productRepo.findById(id).orElse(null);
    }

    public Product addProduct(Product product) throws IOException {
        return productRepo.save(product);
    }

    public Product updatedProduct(int id, Product product, MultipartFile image) throws IOException {
        if(!productRepo.existsById(id)) {
            return null;
        }
        product.setId(id);
        return productRepo.save(product);
    }

    public Product delete(int id) {
        Product product = productRepo.findById(id).orElse(null);

        if(product != null) {
            productRepo.deleteById(id);
        }

        return product;
    }

    public List<Product> searchProducts(String keyword) {
        return productRepo.searchProducts(keyword);
    }
}









