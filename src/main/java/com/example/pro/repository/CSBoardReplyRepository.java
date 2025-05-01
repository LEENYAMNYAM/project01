package com.example.pro.repository;

import com.example.pro.entity.CSBoardEntity;
import com.example.pro.entity.CSBoardReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CSBoardReplyRepository extends JpaRepository<CSBoardReplyEntity, Long> {
    List<CSBoardReplyEntity> findByCsBoard(CSBoardEntity csBoard);
    List<CSBoardReplyEntity> findByCsBoardOrderByRegDateDesc(CSBoardEntity csBoard);
}
