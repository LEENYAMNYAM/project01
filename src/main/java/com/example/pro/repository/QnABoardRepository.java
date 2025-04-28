package com.example.pro.repository;

import com.example.pro.entity.QnABoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QnABoardRepository extends JpaRepository<QnABoardEntity, Long> {
    Page<QnABoardEntity> findByTitleContaining(String keyword, Pageable pageable);

}
