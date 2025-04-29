package com.example.pro.controller;

import com.example.pro.dto.CSBoardDTO;
import com.example.pro.service.CSBoardService;
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
@RequestMapping("/csboard")
public class CSBoardController {

    private final CSBoardService CSBoardService;


    @Autowired
    private CSBoardService csBoardService;

    @GetMapping("/register")
    public String showRegisterForm() {
        return "csboard/register";
    }

    @PostMapping("/register")
    public String createFromForm(@ModelAttribute CSBoardDTO dto) {
        csBoardService.createQBoard(dto);
        return "redirect:/csboard/list";
    }

    @GetMapping("/list")
    public String showList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, 5);
        Page<CSBoardDTO> csPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            csPage = csBoardService.searchCSByTitle(keyword, pageable); // 검색
            model.addAttribute("keyword", keyword); // 검색어 다시 입력창에 유지
        } else {
            csPage = csBoardService.getCSPage(pageable); // 전체 조회
        }

        model.addAttribute("csPage", csPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", csPage.getTotalPages());

        return "csboard/list";
    }

    @GetMapping("/view")
    public String view(@RequestParam Long id, Model model) {
        CSBoardDTO dto = csBoardService.getBoard(id, true); // 조회수 증가 O
        model.addAttribute("cs", dto);
        return "/csboard/view";
    }


    @PostMapping
    public CSBoardDTO create(@RequestBody CSBoardDTO dto) {
        return csBoardService.createQBoard(dto);
    }

    @GetMapping("/update")
    public String updateForm(@RequestParam Long id, Model model) {
        CSBoardDTO dto = csBoardService.getBoard(id, false); // ✅ 조회수 증가 X
        model.addAttribute("cs", dto);
        return "/csboard/update"; // update.html
    }

    @PostMapping("/update/submit")
    public String updateSubmit(@ModelAttribute CSBoardDTO dto) {
        csBoardService.updateQBoard(dto.getId(), dto);
        return "redirect:/csboard/list";
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public String delete(@PathVariable Long id) {
        csBoardService.deleteQBoard(id);
        return "success";
    }


}




