package com.gabriel.SpringEcom.controller;

import com.gabriel.SpringEcom.dto.ProductDTO.ProductImageDTO;
import com.gabriel.SpringEcom.dto.ProductDTO.ProductRequestDTO;
import com.gabriel.SpringEcom.dto.ProductDTO.ProductResponseDTO;
import com.gabriel.SpringEcom.security.UserPrincipal;
import com.gabriel.SpringEcom.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController("products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDTO>> getProducts() {
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.ACCEPTED);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long productId) {
        ProductResponseDTO product = productService.getProductById(productId);

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
    public ResponseEntity<ProductImageDTO> addProductImage(@PathVariable Long id, @RequestParam("file") MultipartFile imageFile) {
        try{
            ProductImageDTO imageSaved =  productService.addProductImage(id, imageFile);
            return new ResponseEntity<>(imageSaved, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/product/images/{imageId}")
    public ResponseEntity<List<ProductImageDTO>> getProductImages(@PathVariable Long imageId) {
        ProductImageDTO images = productService.getProductImages(imageId);

        if(images != null) {
            return ResponseEntity.ok(List.of(images));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/product/{productId}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long productId, @RequestBody ProductRequestDTO productRequest) {
        ProductResponseDTO updatedProduct = productService.updateProduct(productId, productRequest);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(@RequestParam String keyword) {
        List<ProductResponseDTO> products = productService.searchProducts(keyword);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        productService.deleteProductById(productId, userPrincipal.getUser());
        return ResponseEntity.noContent().build();
    }
}
