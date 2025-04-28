package com.example.pro.service;

import com.example.pro.dto.CSBoardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CSBoardService {
    List<CSBoardDTO> getAllQBoards();
    CSBoardDTO getBoard(Long id);
    CSBoardDTO createQBoard(CSBoardDTO dto);
    void updateQBoard(Long id, CSBoardDTO dto);
    void deleteQBoard(Long id);
    CSBoardDTO getBoard(Long id, boolean increaseHitcount);

    // 페이징 추가
    Page<CSBoardDTO> getCSPage(Pageable pageable);
    Page<CSBoardDTO> searchCSByTitle(String keyword, Pageable pageable);
}
