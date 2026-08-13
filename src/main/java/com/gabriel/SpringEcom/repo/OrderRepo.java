package com.gabriel.SpringEcom.repo;

import com.gabriel.SpringEcom.model.Order;
import com.gabriel.SpringEcom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepo extends JpaRepository<Order , Integer> {
    List<Order> findAllByUserExternalId(UUID externalId);
}
