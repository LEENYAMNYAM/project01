package com.example.pro.service;


import com.example.pro.dto.QnABoardDTO;
import com.example.pro.entity.QnABoardEntity;
import com.example.pro.repository.QnABoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public QnABoardDTO getQnaBoardById(Long id) {
        QnABoardEntity board = qnaBoardRepository.findById(id).orElseThrow();
        board.updateHitcount();
        qnaBoardRepository.save(board);
        return toDTO(board);
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
                .qno(board.getQno())
                .title(board.getTitle())
                .writer(board.getWriter())
                .content(board.getContent())
                .createdAt(board.getCreatedAt())
                .hitcount(board.getHitcount())
                .build();
    }
}
