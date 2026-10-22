package com.gabriel.SpringEcom.repo;

import com.gabriel.SpringEcom.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByExternalId(UUID externalId);
    boolean existsByExternalId(UUID externalId);

    @Query("""
        SELECT u FROM User u
        WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(u.email)    LIKE LOWER(CONCAT('%', :keyword, '%'))
        """)
    Page<User> searchUser(@Param("keyword") String keyword, Pageable pageable);
}
