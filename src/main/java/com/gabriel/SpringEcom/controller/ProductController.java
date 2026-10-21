package com.gabriel.SpringEcom.controller;

import com.gabriel.SpringEcom.dto.ProductDTO.ProductImageDTO;
import com.gabriel.SpringEcom.dto.ProductDTO.ProductRequestDTO;
import com.gabriel.SpringEcom.dto.ProductDTO.ProductResponseDTO;
import com.gabriel.SpringEcom.security.UserPrincipal;
import com.gabriel.SpringEcom.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public PagedModel<ProductResponseDTO> getProducts(
            @ParameterObject @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return new PagedModel<>(productService.getAllProducts(pageable));
    }

    @GetMapping("/me")
    public PagedModel<ProductResponseDTO> getMyProducts(
            @AuthenticationPrincipal UserPrincipal principal,
            @ParameterObject @PageableDefault(size = 20, sort = "name") Pageable pageable) { //@ParameterObject para deixar a interface do swagger correta
        return new PagedModel<>(productService.getMyProducts(principal.getUser(), pageable));
    }

    @GetMapping("/search")
    public PagedModel<ProductResponseDTO> searchProducts(
            @RequestParam @NotBlank String keyword,
            @ParameterObject @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return new PagedModel<>(productService.searchProducts(keyword, pageable));
    }

    @GetMapping("/{productId}")
    public ProductResponseDTO getProductById(@PathVariable Long productId) {
        return productService.getProductById(productId);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> addProduct(
            @Valid @RequestBody ProductRequestDTO request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.addProduct(request, principal.getUser()));
    }

    @PutMapping("/{productId}")
    public ProductResponseDTO updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductRequestDTO request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return productService.updateProduct(productId, request, principal.getUser());
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserPrincipal principal) {
        productService.deleteProductById(productId, principal.getUser());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{productId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductImageDTO> addProductImage(
            @PathVariable Long productId,
            @RequestPart("file") MultipartFile imageFile,
            @AuthenticationPrincipal UserPrincipal principal) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.addProductImage(productId, imageFile, principal.getUser()));
    }

    @GetMapping("{/productId}/images")
    public PagedModel<ProductImageDTO> getProductImages() {

    }

    @GetMapping("/images/{imageId}")
    public ProductImageDTO getProductImage(@PathVariable Long imageId) {
        return productService.getProductImage(imageId);
    }
}