package com.gabriel.SpringEcom.repo;

import com.gabriel.SpringEcom.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem, Long> {

    @Modifying
    @Query("DELETE FROM cart_item c WHERE c.product.id = :productId")
    void deleteAllByProductId(@Param("productId") Long productId);

}
