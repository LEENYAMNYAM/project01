package com.example.pro.repository;

import com.example.pro.entity.PaymentEntity;
import com.example.pro.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    List<PaymentEntity> findByCartEntity_User(UserEntity user);
}