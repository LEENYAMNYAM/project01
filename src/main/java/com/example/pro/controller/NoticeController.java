package com.example.pro.controller;

import com.example.pro.dto.NoticeDTO;
import com.example.pro.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping
    public List<NoticeDTO> getAll() {
        return noticeService.getAllNotices();
    }

    @PostMapping
    public NoticeDTO create(@RequestBody NoticeDTO dto) {
        return noticeService.createNotice(dto);
    }

    @PutMapping("/update")
    public void update(@PathVariable Long id, @RequestBody NoticeDTO dto) {
        noticeService.updateNotice(id, dto);
    }

    @DeleteMapping("/delete")
    public void delete(@PathVariable Long id) {
        noticeService.deleteNotice(id);
    }
}
