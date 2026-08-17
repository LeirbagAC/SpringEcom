package com.gabriel.SpringEcom.service;

import com.gabriel.SpringEcom.dto.ProductDTO.ProductRequestDTO;
import com.gabriel.SpringEcom.dto.ProductDTO.ProductResponseDTO;
import com.gabriel.SpringEcom.dto.ProductDTO.ProductImageDTO;
import com.gabriel.SpringEcom.mappers.ProductMapper;
import com.gabriel.SpringEcom.model.Product;
import com.gabriel.SpringEcom.model.ProductImage;
import com.gabriel.SpringEcom.model.User;
import com.gabriel.SpringEcom.repo.CartItemRepo;
import com.gabriel.SpringEcom.repo.ProductImageRepo;
import com.gabriel.SpringEcom.repo.ProductRepo;
import com.gabriel.SpringEcom.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserRepo userRepo;
    private final CartItemRepo cartItemRepo;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts() {
        return productRepo.findByActiveTrue()
                .stream()
                .map(productMapper::mapToProductResponseDTO)
                .toList();
    }

    public ProductResponseDTO getProductById(Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        return productMapper.mapToProductResponseDTO(product);
    }

    @Transactional
    public ProductResponseDTO addProduct(ProductRequestDTO request) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + userEmail));

        Product product = new Product();
        productMapper.updateProductFromRequest(request, product);
        product.setReleaseDate(java.time.LocalDate.now());
        product.setUser(user);

        Product savedProduct = productRepo.save(product);
        return productMapper.mapToProductResponseDTO(savedProduct);
    }

    @Transactional
    public ProductImageDTO addProductImage (Long productId, MultipartFile image) throws IOException {
        Product product  = productRepo.findById(productId).orElseThrow(() -> new RuntimeException("Produto não encontrado: " + productId));

        ProductImage productImage = new ProductImage();

        productImage.setImageName(image.getOriginalFilename());
        productImage.setImageType(image.getContentType());
        productImage.setImageData(image.getBytes());
        productImage.setProduct(product);

        ProductImage savedProductImage = productImageRepo.save(productImage);
        return productMapper.mapToProductImageDTO(savedProductImage);
    }

    public ProductImageDTO getProductImages(Long imageId) {
        ProductImage productImage = productImageRepo.findById(imageId).orElseThrow(() -> new RuntimeException("Imagem não encontrada: " + imageId));
        return productMapper.mapToProductImageDTO(productImage);
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long productId, ProductRequestDTO productRequest) {
        Product product  = productRepo.findById(productId).orElseThrow(() -> new RuntimeException("Produto não encontrado: " + productId));
        productMapper.updateProductFromRequest(productRequest, product);
        Product updatedProduct = productRepo.save(product); //O save() aqui é redundante por conta do transaction, o Dirty Checking já faria o update, mas para fins educacionais preferir deixar.
        return productMapper.mapToProductResponseDTO(updatedProduct);
    }

    @Transactional
    public void deleteProductById(Long productId, User loggedUser) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + productId));

        if(!product.getUser().getId().equals(loggedUser.getId())) throw new RuntimeException("Usuário não autorizado a deletar este produto");

        product.setActive(false);
        product.setStockQuantity(0);
        cartItemRepo.deleteAllByProductId(productId);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> searchProducts(String keyword) {
        return productRepo.searchProducts(keyword)
                .stream()
                .map(productMapper::mapToProductResponseDTO)
                .toList();
    }
}