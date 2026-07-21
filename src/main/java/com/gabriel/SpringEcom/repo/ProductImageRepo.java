package com.gabriel.SpringEcom.repo;

import com.gabriel.SpringEcom.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductImageRepo extends JpaRepository<ProductImage, Long> {

}
