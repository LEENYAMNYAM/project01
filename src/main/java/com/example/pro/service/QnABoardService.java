package com.example.pro.service;

import com.example.pro.dto.QnABoardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QnABoardService {
    List<QnABoardDTO> getAllQBoards();
    QnABoardDTO getBoard(Long id);
    QnABoardDTO createQBoard(QnABoardDTO dto);
    void updateQBoard(Long id, QnABoardDTO dto);
    void deleteQBoard(Long id);

    // ✅ 페이징 추가
    Page<QnABoardDTO> getQnaPage(Pageable pageable);
}
