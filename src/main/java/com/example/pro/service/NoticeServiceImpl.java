package com.example.pro.service;

import com.example.pro.dto.NoticeDTO;
import com.example.pro.entity.NoticeEntity;
import com.example.pro.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public NoticeDTO getNoticeById(Long id) {
        NoticeEntity noticeEntity = noticeRepository.findById(id).orElseThrow();
        return toDTO(noticeEntity);
    }

    @Override
    public NoticeDTO createNotice(NoticeDTO dto) {
        NoticeEntity noticeEntity = new NoticeEntity();
        noticeEntity.change(dto.title, dto.content, dto.important);
        return toDTO(noticeRepository.save(noticeEntity));
    }

    @Override
    public void updateNotice(Long id, NoticeDTO dto) {
        NoticeEntity noticeEntity = noticeRepository.findById(id).orElseThrow();
        noticeEntity.change(dto.title, dto.content, dto.important);
        noticeRepository.save(noticeEntity);
    }

    @Override
    public void deleteNotice(Long id) {
        noticeRepository.deleteById(id);
    }

    private NoticeDTO toDTO(NoticeEntity noticeEntity) {
        NoticeDTO dto = new NoticeDTO();
        dto.id = noticeEntity.getId();
        dto.title = noticeEntity.getTitle();
        dto.writer = noticeEntity.getWriter();
        dto.content = noticeEntity.getContent();
        dto.important = noticeEntity.isImportant();
        return dto;
    }
}
