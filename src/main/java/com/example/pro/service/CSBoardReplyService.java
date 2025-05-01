package com.example.pro.service;

import com.example.pro.dto.CSBoardReplyDTO;
import com.example.pro.entity.CSBoardEntity;
import com.example.pro.entity.CSBoardReplyEntity;

import java.util.List;

public interface CSBoardReplyService {
    // Create a new reply
    CSBoardReplyDTO createReply(CSBoardReplyDTO replyDTO);
    
    // Get all replies for a csboard post
    List<CSBoardReplyDTO> getRepliesByCSBoard(Long csBoardId);
    
    // Update a reply
    CSBoardReplyDTO updateReply(Long id, CSBoardReplyDTO replyDTO);
    
    // Delete a reply
    void deleteReply(Long id);
    
    // Convert entity to DTO
    CSBoardReplyDTO entityToDto(CSBoardReplyEntity entity);
    
    // Convert DTO to entity
    CSBoardReplyEntity dtoToEntity(CSBoardReplyDTO dto);
}