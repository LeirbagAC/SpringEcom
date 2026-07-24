package com.gabriel.SpringEcom.controller;

import com.gabriel.SpringEcom.dto.ProductDTO.ProductImageDTO;
import com.gabriel.SpringEcom.dto.ProductDTO.ProductRequestDTO;
import com.gabriel.SpringEcom.dto.ProductDTO.ProductResponseDTO;
import com.gabriel.SpringEcom.model.Product;
import com.gabriel.SpringEcom.service.ProductService;
import com.gabriel.SpringEcom.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final UserRepository userRepository;

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDTO>> getProducts() {
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.ACCEPTED);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable int id) {
        ProductResponseDTO product = productService.getProductById(id);

        if(product != null) {
            return new ResponseEntity<>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/product")
    public ResponseEntity<ProductResponseDTO> addProduct(@RequestBody ProductRequestDTO productRequest) {
        ProductResponseDTO savedProduct = productService.addProduct(productRequest);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @PostMapping(value = "/product/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductImageDTO> addProductImage(@PathVariable int id, @RequestParam("file") MultipartFile imageFile) {
        try{
            ProductImageDTO imageSaved =  productService.addProductImage(id, imageFile);
            return new ResponseEntity<>(imageSaved, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/product/images/{id}")
    public ResponseEntity<List<ProductImageDTO>> getProductImages(@PathVariable Long id) {
        ProductImageDTO images = productService.getProductImages(id);

        if(images != null) {
            return ResponseEntity.ok(List.of(images));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable int id, @RequestBody ProductRequestDTO productRequest) {
        ProductResponseDTO updatedProduct = productService.updateProduct(id, productRequest);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(@RequestParam String keyword) {
        List<ProductResponseDTO> products = productService.searchProducts(keyword);

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<Product> delete(@PathVariable int id) {
        Product deletedProduct = productService.delete(id);

        if(deletedProduct != null) {
            return new ResponseEntity<>(deletedProduct, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
