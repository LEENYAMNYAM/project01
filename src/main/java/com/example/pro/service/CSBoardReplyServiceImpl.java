package com.example.pro.service;

import com.example.pro.dto.CSBoardReplyDTO;
import com.example.pro.entity.CSBoardEntity;
import com.example.pro.entity.CSBoardReplyEntity;
import com.example.pro.entity.UserEntity;
import com.example.pro.repository.CSBoardReplyRepository;
import com.example.pro.repository.CSBoardRepository;
import com.example.pro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CSBoardReplyServiceImpl implements CSBoardReplyService {

    @Autowired
    private CSBoardReplyRepository replyRepository;

    @Autowired
    private CSBoardRepository csBoardRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public CSBoardReplyDTO createReply(CSBoardReplyDTO replyDTO) {
        CSBoardReplyEntity entity = dtoToEntity(replyDTO);
        CSBoardReplyEntity savedEntity = replyRepository.save(entity);
        return entityToDto(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CSBoardReplyDTO> getRepliesByCSBoard(Long csBoardId) {
        CSBoardEntity csBoard = csBoardRepository.findById(csBoardId)
                .orElseThrow(() -> new RuntimeException("CSBoard not found with id: " + csBoardId));

        List<CSBoardReplyEntity> replies = replyRepository.findByCsBoardOrderByRegDateDesc(csBoard);

        return replies.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CSBoardReplyDTO updateReply(Long id, CSBoardReplyDTO replyDTO) {
        CSBoardReplyEntity entity = replyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reply not found with id: " + id));

        entity.setContent(replyDTO.getContent());

        CSBoardReplyEntity updatedEntity = replyRepository.save(entity);
        return entityToDto(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteReply(Long id) {
        replyRepository.deleteById(id);
    }

    @Override
    public CSBoardReplyDTO entityToDto(CSBoardReplyEntity entity) {
        return CSBoardReplyDTO.builder()
                .id(entity.getId())
                .csBoardId(entity.getCsBoard().getId())
                .content(entity.getContent())
                .writer(entity.getWriter())
                .user(entity.getUser())
                .createdAt(entity.getRegDate())
                .updatedAt(entity.getUpdateDate())
                .build();
    }

    @Override
    public CSBoardReplyEntity dtoToEntity(CSBoardReplyDTO dto) {
        CSBoardEntity csBoard = csBoardRepository.findById(dto.getCsBoardId())
                .orElseThrow(() -> new RuntimeException("CSBoard not found with id: " + dto.getCsBoardId()));

        UserEntity user = null;
        if (dto.getUser() != null) {
            user = userRepository.findByUsername(dto.getUser().getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found with username: " + dto.getUser().getUsername()));
        }

        CSBoardReplyEntity entity = new CSBoardReplyEntity();
        entity.setId(dto.getId());
        entity.setCsBoard(csBoard);
        entity.setContent(dto.getContent());
        entity.setWriter(dto.getWriter());
        entity.setUser(user);

        return entity;
    }
}
