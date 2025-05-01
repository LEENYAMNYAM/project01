package com.example.pro.repository;

import com.example.pro.entity.CartEntity;
import com.example.pro.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
    List<CartEntity> findByUserEntity_Username(String username);
    List<CartEntity> findByUserEntity(UserEntity userEntity);

}
