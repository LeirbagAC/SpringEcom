package com.gabriel.SpringEcom.service;

import com.gabriel.SpringEcom.model.Product;
import com.gabriel.SpringEcom.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepo productRepo;

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Product getProductById(int id) {
        return productRepo.findById(id).orElse(null);
    }

    public Product addProduct(Product product, MultipartFile image) throws IOException {
        product.setImageName(image.getOriginalFilename());
        product.setImageType(image.getContentType());
        product.setImageData(image.getBytes());

        return productRepo.save(product);
    }

    public Product updatedProduct(int id, Product product, MultipartFile image) throws IOException {
        if(!productRepo.existsById(id)) {
            return null;
        }

        product.setId(id);

        product.setImageName(image.getOriginalFilename());
        product.setImageType(image.getContentType());
        product.setImageData(image.getBytes());
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









