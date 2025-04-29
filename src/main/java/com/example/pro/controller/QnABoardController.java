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
    public String showList(@RequestParam(value = "page", defaultValue = "0") int page,
                           Model model) {
        Pageable pageable = PageRequest.of(page, 5); // 한 페이지당 10개
        Page<QnABoardDTO> qnaPage = qnaBoardService.getQnaPage(pageable);

        model.addAttribute("qnaPage", qnaPage);           // 페이징된 리스트
        model.addAttribute("currentPage", page);          // 현재 페이지 번호
        model.addAttribute("totalPages", qnaPage.getTotalPages()); // 전체 페이지 수

        return "/qnaboard/list";
    }

    @GetMapping("/view")
    public String view(@RequestParam Long id, Model model) {
        QnABoardDTO dto = QnABoardService.getBoard(id);
        model.addAttribute("qna", dto);
        return "/qnaboard/view";
    }


    @PostMapping
    public QnABoardDTO create(@RequestBody QnABoardDTO dto) {
        return qnaBoardService.createQBoard(dto);
    }

    @GetMapping("/update")
    public String updateForm(@RequestParam Long id, Model model) {
        QnABoardDTO dto = qnaBoardService.getBoard(id);
        model.addAttribute("qna", dto);
        return "/qnaboard/update"; // update.html
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




