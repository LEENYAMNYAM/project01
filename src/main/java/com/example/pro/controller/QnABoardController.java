package com.example.pro.controller;

import com.example.pro.dto.QnABoardDTO;
import com.example.pro.service.QnABoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@Controller
@RequestMapping("/qnaboard")
public class QnABoardController {

    private final QnABoardService QnABoardService;


    @Autowired
    private QnABoardService qnaBoardService;

    @GetMapping("/register")
    public String showRegisterForm() {
        return "qnaboard/register";
    }

    @PostMapping("/register")
    public String createFromForm(@ModelAttribute QnABoardDTO dto) {
        qnaBoardService.createQBoard(dto);
        return "redirect:/qnaboard/list";
    }

    @GetMapping("/list")
    public String showList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, 5);
        Page<QnABoardDTO> qnaPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            qnaPage = qnaBoardService.searchQnAByTitle(keyword, pageable); // 검색
            model.addAttribute("keyword", keyword); // 검색어 다시 입력창에 유지
        } else {
            qnaPage = qnaBoardService.getQnaPage(pageable); // 전체 조회
        }

        model.addAttribute("qnaPage", qnaPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", qnaPage.getTotalPages());

        return "qnaboard/list";
    }

    @GetMapping("/view")
    public String view(@RequestParam Long id, Model model) {
        QnABoardDTO dto = qnaBoardService.getBoard(id, true); // 조회수 증가 O
        model.addAttribute("qna", dto);
        return "qnaboard/view";
    }


    @PostMapping
    public QnABoardDTO create(@RequestBody QnABoardDTO dto) {
        return qnaBoardService.createQBoard(dto);
    }

    @GetMapping("/update")
    public String updateForm(@RequestParam Long id, Model model) {
        QnABoardDTO dto = qnaBoardService.getBoard(id, false); // ✅ 조회수 증가 X
        model.addAttribute("qna", dto);
        return "qnaboard/update"; // update.html
    }

    @PostMapping("/update/submit")
    public String updateSubmit(@ModelAttribute QnABoardDTO dto) {
        qnaBoardService.updateQBoard(dto.getId(), dto);
        return "redirect:/qnaboard/list";
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public String delete(@PathVariable Long id) {
        qnaBoardService.deleteQBoard(id);
        return "success";
    }


}




