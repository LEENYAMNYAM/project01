package com.example.pro.repository;

import com.example.pro.entity.CartEntity;
import com.example.pro.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {
    Optional<CartEntity> findByUser(UserEntity user);
}