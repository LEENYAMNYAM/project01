package com.example.pro.controller;

import com.example.pro.dto.NoticeDTO;
import com.example.pro.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping
    public @ResponseBody List<NoticeDTO> getAll() {
        return noticeService.getAllNotices();
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("notice", new NoticeDTO());
        return "notice/register";
    }

    @PostMapping
    public String create(@ModelAttribute NoticeDTO dto) {
        noticeService.createNotice(dto);
        return "redirect:/notice/list";
    }

    @GetMapping("/view")
    public String view(@RequestParam Long id, Model model) {
        NoticeDTO notice = noticeService.getNoticeById(id);
        model.addAttribute("notice", notice);
        return "notice/view";
    }

    @GetMapping("/update")
    public String updateForm(@RequestParam Long id, Model model) {
        NoticeDTO notice = noticeService.getNoticeById(id);
        model.addAttribute("notice", notice);
        return "notice/update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute NoticeDTO dto) {
        noticeService.updateNotice(dto.id, dto);
        return "redirect:/notice/view?id=" + dto.id;
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public String delete(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return "success";
    }

    @GetMapping("/list")
    public String listPage(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, 5); // 한 페이지에 5개씩

        Page<NoticeDTO> noticePage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            noticePage = noticeService.searchNoticesByTitle(keyword, pageable); // ✅ 핵심 수정
            model.addAttribute("keyword", keyword);
        } else {
            noticePage = noticeService.getNoticePage(pageable);
        }

        model.addAttribute("noticePage", noticePage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", noticePage.getTotalPages());

        return "notice/list";
    }
}
