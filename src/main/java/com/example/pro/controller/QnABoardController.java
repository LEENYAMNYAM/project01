package com.example.pro.controller;


import com.example.pro.dto.QnABoardDTO;
import com.example.pro.service.QnABoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/qBoard")
public class QnABoardController {

    @Autowired
    private QnABoardService qnaBoardService;

    @GetMapping("/list")
    public List<QnABoardDTO> getAll() {
        return qnaBoardService.getAllQBoards();
    }

    @PostMapping
    public QnABoardDTO create(@RequestBody QnABoardDTO dto) {
        return qnaBoardService.createQBoard(dto);
    }

    @PutMapping("/update")
    public void update(@PathVariable Long id, @RequestBody QnABoardDTO dto) {
        qnaBoardService.updateQBoard(id, dto);
    }

    @DeleteMapping("/delete")
    public void delete(@PathVariable Long id) {
        qnaBoardService.deleteQBoard(id);
    }



}
