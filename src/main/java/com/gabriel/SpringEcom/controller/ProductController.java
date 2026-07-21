package com.gabriel.SpringEcom.controller;

import com.gabriel.SpringEcom.dto.ProductDTO.ProductDTO;
import com.gabriel.SpringEcom.model.Product;
import com.gabriel.SpringEcom.model.ProductImage;
import com.gabriel.SpringEcom.service.ProductService;
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

    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> getProducts() {
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.ACCEPTED);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable int id) {
        ProductDTO product = productService.getProductById(id);

        if(product != null) {
            return new ResponseEntity<>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/product")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product savedProduct = productService.addProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @PostMapping(value = "/product/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProductImage(@PathVariable int id, @RequestParam("file") MultipartFile imageFile) {
        try{
            ProductImage imageSaved =  productService.addProductImage(id, imageFile);
            return new ResponseEntity<>(imageSaved, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/product/images/{id}")
    public ResponseEntity<List<ProductImage>> getProductImages(@PathVariable Long id) {
        ProductImage images = productService.getProductImages(id);

        if(images != null) {
            return ResponseEntity.ok(List.of(images));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable int id, @RequestBody Product product) {
        try{
            Product updatedProduct = productService.updatedProduct(id, product);

            if(updatedProduct != null) {
                return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Produto não encontrado", HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (IOException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String keyword) {
        List<ProductDTO> products = productService.searchProducts(keyword);

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
