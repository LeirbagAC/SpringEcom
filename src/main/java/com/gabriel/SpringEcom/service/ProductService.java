package com.gabriel.SpringEcom.service;

import com.gabriel.SpringEcom.dto.ProductDTO.ProductImageResponseDTO;
import com.gabriel.SpringEcom.dto.ProductDTO.ProductRequestDTO;
import com.gabriel.SpringEcom.dto.ProductDTO.ProductResponseDTO;
import com.gabriel.SpringEcom.exceptions.BusinessRuleException;
import com.gabriel.SpringEcom.exceptions.ForbiddenOperationException;
import com.gabriel.SpringEcom.exceptions.NotFoundException;
import com.gabriel.SpringEcom.mappers.ProductMapper;
import com.gabriel.SpringEcom.model.Product;
import com.gabriel.SpringEcom.model.ProductImage;
import com.gabriel.SpringEcom.model.User;
import com.gabriel.SpringEcom.model.enums.Role;
import com.gabriel.SpringEcom.repo.CartItemRepo;
import com.gabriel.SpringEcom.repo.ProductImageRepo;
import com.gabriel.SpringEcom.repo.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final ProductRepo productRepo;
    private final ProductImageRepo productImageRepo;
    private final CartItemRepo cartItemRepo;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {
        return productRepo.findByActiveTrue(pageable)
                .map(productMapper::mapToProductResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getMyProducts(User loggedUser, Pageable pageable) {
        return productRepo.findByUserAndActiveTrue(loggedUser, pageable)
                .map(productMapper::mapToProductResponseDTO);
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long productId) {
        return productMapper.mapToProductResponseDTO(getActiveProduct(productId));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> searchProducts(String keyword, Pageable pageable) {
        return productRepo.searchProducts(keyword.trim(), pageable)
                .map(productMapper::mapToProductResponseDTO);
    }

    @Transactional
    public ProductResponseDTO addProduct(ProductRequestDTO request, User user) {
        Product product = new Product();
        productMapper.updateProductFromRequest(request, product);
        product.setReleaseDate(LocalDate.now());
        product.setUser(user);

        return productMapper.mapToProductResponseDTO(productRepo.save(product));
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long productId, ProductRequestDTO request, User loggedUser) {
        Product product = getActiveProduct(productId);
        assertCanModify(product, loggedUser);

        productMapper.updateProductFromRequest(request, product);
        return productMapper.mapToProductResponseDTO(product);
    }

    @Transactional
    public void deleteProductById(Long productId, User loggedUser) {
        Product product = getActiveProduct(productId);
        assertCanModify(product, loggedUser);

        product.setActive(false);
        product.setStockQuantity(0);
        cartItemRepo.deleteAllByProductId(productId);
    }

    @Transactional
    public ProductImageResponseDTO addProductImage(Long productId, MultipartFile image, User loggedUser) throws IOException {
        Product product = getActiveProduct(productId);
        assertCanModify(product, loggedUser);
        validateImage(image);

        ProductImage productImage = new ProductImage();
        productImage.setImageName(image.getOriginalFilename());
        productImage.setImageType(image.getContentType());
        productImage.setImageData(image.getBytes());
        productImage.setProduct(product);

        return productMapper.mapToProductImageDTO(productImageRepo.save(productImage));
    }

    @Transactional(readOnly = true)
    public ProductImageResponseDTO getProductImage(Long imageId) {
        ProductImage image = productImageRepo.findById(imageId)
                .orElseThrow(() -> new NotFoundException("Imagem", imageId));
        return productMapper.mapToProductImageDTO(image);
    }

    public List<ProductImageResponseDTO> getImagesByProduct(Long productId) {
        Product product = getActiveProduct(productId);
        List<ProductImage> images = productImageRepo.findProductImagesByProduct(product);
        return images.stream()
                .map(productMapper::mapToProductImageDTO)
                .toList();
    }

    // Métodos Auxiliares

    private Product getActiveProduct(Long productId) {
        return productRepo.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new NotFoundException("Produto", productId));
    }

    private void assertCanModify(Product product, User loggedUser) {
        boolean isOwner = product.getUser().getId().equals(loggedUser.getId());
        boolean isAdmin = loggedUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw ForbiddenOperationException.notOwner("produto");
        }
    }

    private void validateImage(MultipartFile image) {
        if (image.isEmpty()) {
            throw new BusinessRuleException("O arquivo de imagem está vazio.");
        }
        if (!ALLOWED_IMAGE_TYPES.contains(image.getContentType())) {
            throw new BusinessRuleException("Formato de imagem não suportado. Use JPEG, PNG ou WebP.");
        }
    }
}