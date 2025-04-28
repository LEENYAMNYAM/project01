package com.example.pro.service;

import com.example.pro.dto.QnABoardDTO;
import com.example.pro.entity.QnABoardEntity;
import com.example.pro.repository.QnABoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QnABoardServiceImpl implements QnABoardService {

    @Autowired
    private QnABoardRepository qnaBoardRepository;

    @Override
    public List<QnABoardDTO> getAllQBoards() {
        return qnaBoardRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // 페이징 메서드 추가
    @Override
    public Page<QnABoardDTO> getQnaPage(Pageable pageable) {
        return qnaBoardRepository.findAll(pageable)
                .map(this::toDTO);
    }

    @Override
    public Page<QnABoardDTO> searchQnAByTitle(String keyword, Pageable pageable) {
        return qnaBoardRepository
                .findByTitleContaining(keyword, pageable)
                .map(this::toDTO);
    }

    // 기본 getBoard: 조회수 증가 true로 기본 처리
    @Override
    public QnABoardDTO getBoard(Long id) {
        return getBoard(id, true);
    }

    // 조회수 증가 여부를 제어하는 getBoard
    public QnABoardDTO getBoard(Long id, boolean increaseHitcount) {
        QnABoardEntity entity = qnaBoardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QnABoard not found with id: " + id));

        if (increaseHitcount) {
            entity.updateHitcount();
            qnaBoardRepository.save(entity);
        }

        return toDTO(entity);
    }

    @Override
    public QnABoardDTO createQBoard(QnABoardDTO dto) {
        QnABoardEntity board = new QnABoardEntity();
        board.setTitle(dto.getTitle());
        board.setWriter(dto.getWriter());
        board.setContent(dto.getContent());
        return toDTO(qnaBoardRepository.save(board));
    }

    @Override
    public void updateQBoard(Long id, QnABoardDTO dto) {
        QnABoardEntity board = qnaBoardRepository.findById(id).orElseThrow();
        board.change(dto.getTitle(), dto.getContent());
        qnaBoardRepository.save(board);
    }

    @Override
    public void deleteQBoard(Long id) {
        qnaBoardRepository.deleteById(id);
    }

    private QnABoardDTO toDTO(QnABoardEntity board) {
        return QnABoardDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .writer(board.getWriter())
                .content(board.getContent())
                .createdAt(board.getCreatedAt())
                .hitcount(board.getHitcount())
                .build();
    }
}
