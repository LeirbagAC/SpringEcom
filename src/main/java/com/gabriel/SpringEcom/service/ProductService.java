package com.gabriel.SpringEcom.service;

import com.gabriel.SpringEcom.dto.ProductDTO.ProductDTO;
import com.gabriel.SpringEcom.dto.ProductDTO.ProductImageDTO;
import com.gabriel.SpringEcom.model.Product;
import com.gabriel.SpringEcom.model.ProductImage;
import com.gabriel.SpringEcom.repo.ProductImageRepo;
import com.gabriel.SpringEcom.repo.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepo productRepo;
    private final ProductImageRepo productImageRepo;

    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepo.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public ProductDTO getProductById(Integer id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        return toDTO(product);
    }

    public Product addProduct(Product product){
        return productRepo.save(product);
    }

    public ProductImage addProductImage (int productId, MultipartFile image) throws IOException {
        Product product  = productRepo.findById(productId).orElseThrow(() -> new RuntimeException("Produto não encontrado " + productId));

        ProductImage productImage = new ProductImage();
        productImage.setImageName(image.getOriginalFilename());
        productImage.setImageType(image.getContentType());
        productImage.setImageData(image.getBytes());
        productImage.setProduct(product);

        return productImageRepo.save(productImage);
    }

    public ProductImage getProductImages(Long id) {
        return productImageRepo.findById(id).orElse(null);
    }

    public Product updatedProduct(int id, Product product) throws IOException {
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

    public List<ProductDTO> searchProducts(String keyword) {
        return productRepo.searchProducts(keyword)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    //Método privado auxiliar
    private ProductDTO toDTO(Product product) {
        List<ProductImageDTO> imageDTOs = product.getImages().stream().map(image ->
                new ProductImageDTO(
                        image.getId(),
                        image.getImageName(),
                        image.getImageType(),
                        "http://localhost:8080/product/images/" + image.getId()
                )
        ).toList();

        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.isProductAvailable(),
                imageDTOs
        );
    }
}