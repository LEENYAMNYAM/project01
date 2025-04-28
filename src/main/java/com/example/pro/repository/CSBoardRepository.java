package com.example.pro.repository;

import com.example.pro.entity.CSBoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CSBoardRepository extends JpaRepository<CSBoardEntity, Long> {
    Page<CSBoardEntity> findByTitleContaining(String keyword, Pageable pageable);

}
