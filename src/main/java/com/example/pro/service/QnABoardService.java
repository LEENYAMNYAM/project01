package com.example.pro.service;


import com.example.pro.dto.QnABoardDTO;

import java.util.List;

public interface QnABoardService {
    List<QnABoardDTO> getAllQBoards();
    QnABoardDTO getQnABoardById(Long id);
    QnABoardDTO createQBoard(QnABoardDTO dto);
    void updateQBoard(Long id, QnABoardDTO dto);
    void deleteQBoard(Long id);
}
