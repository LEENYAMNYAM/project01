package com.example.pro.repository;

import com.example.pro.entity.NoticeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<NoticeEntity, Long> {

    // 기존: 전체 검색용
    List<NoticeEntity> findByTitleContaining(String keyword);

    // 추가: 페이징 가능한 제목 검색
    Page<NoticeEntity> findByTitleContaining(String keyword, Pageable pageable);
}

