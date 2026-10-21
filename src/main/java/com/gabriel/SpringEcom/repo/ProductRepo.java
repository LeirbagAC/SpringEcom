package com.gabriel.SpringEcom.repo;

import com.gabriel.SpringEcom.model.Product;
import com.gabriel.SpringEcom.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

    Page<Product> findByActiveTrue(Pageable pageable);

    Page<Product> findByUserAndActiveTrue(User user, Pageable pageable);

    Optional<Product> findByIdAndActiveTrue(Long id);

    @Query("""
            SELECT p FROM Product p
            WHERE p.active = true AND (
                LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);
}