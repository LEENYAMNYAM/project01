package com.example.pro.service;

import com.example.pro.dto.NoticeDTO;
import com.example.pro.entity.NoticeEntity;
import com.example.pro.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    @Override
    public List<NoticeDTO> getAllNotices() {
        return noticeRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<NoticeDTO> getNoticePage(Pageable pageable) {
        return noticeRepository.findAll(pageable)
                .map(this::toDTO);
    }

    @Override
    public Page<NoticeDTO> searchNoticesByTitle(String keyword, Pageable pageable) {
        return noticeRepository.findByTitleContaining(keyword, pageable)
                .map(this::toDTO);
    }

    @Override
    public NoticeDTO getNoticeById(Long id, boolean increaseHitcount) {
        NoticeEntity entity = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found with id: " + id));

        if (increaseHitcount) {
            entity.updateHitcount();
            noticeRepository.save(entity);
        }

        return toDTO(entity);
    }

    @Override
    public NoticeDTO getNoticeById(Long id) {
        return getNoticeById(id, true); // 기본 조회는 조회수 증가
    }

    @Override
    public NoticeDTO createNotice(NoticeDTO dto, String username) {
        NoticeEntity noticeEntity = new NoticeEntity();
        noticeEntity.change(dto.title, dto.content, dto.important, username);
        return toDTO(noticeRepository.save(noticeEntity));
    }

    @Override
    public void updateNotice(Long id, NoticeDTO dto) {
        NoticeEntity noticeEntity = noticeRepository.findById(id).orElseThrow();
        noticeEntity.change(dto.title, dto.content, dto.important, dto.writer);
        noticeRepository.save(noticeEntity);
    }

    @Override
    public void deleteNotice(Long id) {
        noticeRepository.deleteById(id);
    }

    @Override
    public List<NoticeDTO> searchNoticesByTitle(String keyword) {
        return noticeRepository.findByTitleContaining(keyword)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ Entity → DTO 변환
    private NoticeDTO toDTO(NoticeEntity noticeEntity) {
        NoticeDTO dto = new NoticeDTO();
        dto.id = noticeEntity.getId();
        dto.title = noticeEntity.getTitle();
        dto.writer = noticeEntity.getWriter();
        dto.content = noticeEntity.getContent();
        dto.important = noticeEntity.isImportant();
        dto.hitcount = noticeEntity.getHitcount();
        return dto;
    }
}
