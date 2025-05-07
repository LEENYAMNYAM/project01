package com.example.pro.service;

import com.example.pro.dto.CSBoardDTO;
import com.example.pro.entity.CSBoardEntity;
import com.example.pro.entity.CSBoardReplyEntity;
import com.example.pro.repository.CSBoardReplyRepository;
import com.example.pro.repository.CSBoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CSBoardServiceImpl implements CSBoardService {

    @Autowired
    private CSBoardRepository csBoardRepository;

    @Autowired
    private CSBoardReplyRepository csBoardReplyRepository;

    @Override
    public List<CSBoardDTO> getAllQBoards() {
        return csBoardRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // 페이징 메서드 추가
    @Override
    public Page<CSBoardDTO> getCSPage(Pageable pageable) {
        return csBoardRepository.findAll(pageable)
                .map(this::toDTO);
    }

    @Override
    public Page<CSBoardDTO> searchCSByTitle(String keyword, Pageable pageable) {
        return csBoardRepository
                .findByTitleContaining(keyword, pageable)
                .map(this::toDTO);
    }

    // 기본 getBoard: 조회수 증가 true로 기본 처리
    @Override
    public CSBoardDTO getBoard(Long id) {
        return getBoard(id, true);
    }

    // 조회수 증가 여부를 제어하는 getBoard
    public CSBoardDTO getBoard(Long id, boolean increaseHitcount) {
        CSBoardEntity entity = csBoardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("csBoard not found with id: " + id));

        if (increaseHitcount) {
            entity.updateHitcount();
            csBoardRepository.save(entity);
        }

        return toDTO(entity);
    }

    @Override
    public CSBoardDTO createQBoard(CSBoardDTO dto) {
        CSBoardEntity board = new CSBoardEntity();
        board.setTitle(dto.getTitle());
        board.setWriter(dto.getWriter());
        board.setContent(dto.getContent());
        return toDTO(csBoardRepository.save(board));
    }

    @Override
    public void updateQBoard(Long id, CSBoardDTO dto) {
        CSBoardEntity board = csBoardRepository.findById(id).orElseThrow();
        board.change(dto.getTitle(), dto.getContent());
        csBoardRepository.save(board);
    }

    @Override
    @Transactional
    public void deleteQBoard(Long id) {
        // Find the board entity
        CSBoardEntity board = csBoardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CSBoard not found with id: " + id));

        // Find all replies associated with this board
        List<CSBoardReplyEntity> replies = csBoardReplyRepository.findByCsBoard(board);

        // Delete all replies first
        for (CSBoardReplyEntity reply : replies) {
            csBoardReplyRepository.delete(reply);
        }

        // Then delete the board post
        csBoardRepository.delete(board);
    }

    private CSBoardDTO toDTO(CSBoardEntity board) {
        return CSBoardDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .writer(board.getWriter())
                .content(board.getContent())
                .createdAt(board.getCreatedAt())
                .hitcount(board.getHitcount())
                .build();
    }
}
