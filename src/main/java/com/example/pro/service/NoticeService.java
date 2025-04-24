package com.example.pro.service;

import com.example.pro.dto.NoticeDTO;

import java.util.List;

public interface NoticeService {
    List<NoticeDTO> getAllNotices();
    NoticeDTO getNoticeById(Long id);
    NoticeDTO createNotice(NoticeDTO dto);
    void updateNotice(Long id, NoticeDTO dto);
    void deleteNotice(Long id);
}
