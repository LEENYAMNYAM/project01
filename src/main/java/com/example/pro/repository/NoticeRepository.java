package com.example.pro.repository;

import com.example.pro.entity.NoticeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<NoticeEntity, Long> {

    // 전체 검색용
    List<NoticeEntity> findByTitleContaining(String keyword);

    // 페이징 가능한 제목 검색
    Page<NoticeEntity> findByTitleContaining(String keyword, Pageable pageable);

    // 최신 3가지 공지사항 객체 가져오기
    List<NoticeEntity> findTop3ByOrderByIdDesc();
}

