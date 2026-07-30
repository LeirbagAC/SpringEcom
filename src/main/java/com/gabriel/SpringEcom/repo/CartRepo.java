package com.gabriel.SpringEcom.repo;

import com.gabriel.SpringEcom.model.Cart;
import com.gabriel.SpringEcom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
