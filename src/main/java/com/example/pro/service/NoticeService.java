package com.example.pro.service;

import com.example.pro.dto.NoticeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NoticeService {
    List<NoticeDTO> getAllNotices();
    NoticeDTO createNotice(NoticeDTO dto, String username);
    NoticeDTO getNoticeById(Long id, boolean increaseHitcount);
    NoticeDTO getNoticeById(Long id);
    List<NoticeDTO> getLatest3Notices();
    void updateNotice(Long id, NoticeDTO dto);
    void deleteNotice(Long id);


    // 제목 키워드로 검색 (기존 방식 - 전체 리스트 반환)
    List<NoticeDTO> searchNoticesByTitle(String keyword);

    // 페이징 전체 목록
    Page<NoticeDTO> getNoticePage(Pageable pageable);

    // 페이징 + 검색 기능
    Page<NoticeDTO> searchNoticesByTitle(String keyword, Pageable pageable);
}
