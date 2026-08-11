package com.gabriel.SpringEcom.service;

import com.gabriel.SpringEcom.dto.ProductDTO.ProductRequestDTO;
import com.gabriel.SpringEcom.dto.ProductDTO.ProductResponseDTO;
import com.gabriel.SpringEcom.dto.ProductDTO.ProductImageDTO;
import com.gabriel.SpringEcom.dto.UserDTO.SellerSummaryDTO;
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

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts() {
        return productRepo.findByActiveTrue()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public ProductResponseDTO getProductById(Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        return toDTO(product);
    }

    @Transactional
    public ProductResponseDTO addProduct(ProductRequestDTO request) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + userEmail));

        Product product = new Product();
        applyRequestToProduct(product, request);
        product.setReleaseDate(java.time.LocalDate.now());
        product.setUser(user);

        Product savedProduct = productRepo.save(product);
        return toDTO(savedProduct);
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
        return toDTOImage(savedProductImage);
    }

    public ProductImageDTO getProductImages(Long imageId) {
        ProductImage productImage = productImageRepo.findById(imageId).orElseThrow(() -> new RuntimeException("Imagem não encontrada: " + imageId));
        return toDTOImage(productImage);
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long productId, ProductRequestDTO productRequest) {
        Product product  = productRepo.findById(productId).orElseThrow(() -> new RuntimeException("Produto não encontrado: " + productId));
        applyRequestToProduct(product, productRequest);
        Product updatedProduct = productRepo.save(product); //O save() aqui é redundante por conta do transaction, o Dirty Checking já faria o update, mas para fins educacionais preferir deixar.
        return toDTO(updatedProduct);
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
                .map(this::toDTO)
                .toList();
    }

    // --- Métodos privados auxiliar na conversão dos DTOs
    private ProductResponseDTO toDTO(Product product) {
        List<ProductImageDTO> imageDTOs = product.getImages().stream().map(image ->
                new ProductImageDTO(
                        image.getId(),
                        image.getImageName(),
                        image.getImageType(),
                        "http://localhost:8080/product/image/" + image.getId()
                )
        ).toList();

        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.isProductAvailable(),
                toDTOUser(product.getUser()),
                imageDTOs
        );
    }

    private SellerSummaryDTO toDTOUser(User user) {
        return new SellerSummaryDTO(
                user.getExternalId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    private ProductImageDTO toDTOImage(ProductImage savedProductImage) {
        return new ProductImageDTO(
                savedProductImage.getId(),
                savedProductImage.getImageName(),
                savedProductImage.getImageType(),
                "http://localhost:8080/product/image/" + savedProductImage.getId()
        );
    }

    //Funçao criada para ajuda na hora de atualizar o produto, para não precisar ficar setando cada campo manualmente
    //Provavelmente não apenasa esse método, mas também os outros podem ser refatorada com alguma lib (ModelMapper ou MapStruct) para fazer isso de forma automática, mas por enquanto vai assim
    private void applyRequestToProduct(Product product, ProductRequestDTO request) {
        product.setName(request.name());
        product.setDescription(request.description());
        product.setBrand(request.brand());
        product.setPrice(request.price());
        product.setCategory(request.category());
        product.setStockQuantity(request.stockQuantity());
    }
}