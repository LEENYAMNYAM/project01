package com.example.pro.controller;

import com.example.pro.config.auth.PrincipalDetail;
import com.example.pro.dto.CSBoardDTO;
import com.example.pro.dto.CSBoardReplyDTO;
import com.example.pro.entity.UserEntity;
import com.example.pro.service.CSBoardReplyService;
import com.example.pro.service.CSBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@Controller
@RequestMapping("/csboard")
@Log4j2
public class CSBoardController {

    private final CSBoardService CSBoardService;


    @Autowired
    private CSBoardService csBoardService;

    @Autowired
    private CSBoardReplyService csBoardReplyService;

    @GetMapping("/register")
    public String showRegisterForm() {
        return "csboard/register";
    }

    @PostMapping("/register")
    public String createFromForm(@ModelAttribute CSBoardDTO dto, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        // Set the writer to the logged-in user's username
        if (principalDetail != null) {
            dto.setWriter(principalDetail.getUsername());
        }
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
    public String view(@RequestParam Long id, Model model, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        CSBoardDTO dto = csBoardService.getBoard(id, true); // 조회수 증가 O
        model.addAttribute("cs", dto);

        // Get replies for this csboard post
        List<CSBoardReplyDTO> replies = csBoardReplyService.getRepliesByCSBoard(id);
        model.addAttribute("replies", replies);

        // Check if the current user is the original poster or an admin
        boolean canReply = false;
        if (principalDetail != null) {
            UserEntity currentUser = principalDetail.getUser();
            // Check if user is the original poster
            boolean isOriginalPoster = dto.getWriter().equals(currentUser.getUsername());
            // Check if user is an admin
            boolean isAdmin = "ROLE_ADMIN".equals(currentUser.getRole());

            canReply = isOriginalPoster || isAdmin;
        }
        model.addAttribute("canReply", canReply);

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


    // Add a reply to a csboard post
    @PostMapping("/reply/{id}")
    public String addReply(@PathVariable Long id,
                          @RequestParam("content") String content,
                          @AuthenticationPrincipal PrincipalDetail principalDetail,
                          Model model) {
        try {
            // Check if user is logged in
            if (principalDetail == null) {
                return "redirect:/login";
            }

            UserEntity currentUser = principalDetail.getUser();
            CSBoardDTO csBoard = csBoardService.getBoard(id, false);

            // Check if user is the original poster or an admin
            boolean isOriginalPoster = csBoard.getWriter().equals(currentUser.getUsername());
            boolean isAdmin = "ROLE_ADMIN".equals(currentUser.getRole());

            if (!isOriginalPoster && !isAdmin) {
                log.warn("Unauthorized reply attempt by: " + currentUser.getUsername());
                return "redirect:/csboard/view?id=" + id + "&error=unauthorized";
            }

            // Create the reply
            CSBoardReplyDTO replyDTO = new CSBoardReplyDTO();
            replyDTO.setCsBoardId(id);
            replyDTO.setContent(content);
            replyDTO.setWriter(currentUser.getUsername());
            replyDTO.setUser(currentUser);

            csBoardReplyService.createReply(replyDTO);

            return "redirect:/csboard/view?id=" + id;
        } catch (Exception e) {
            log.error("Error adding reply: " + e.getMessage());
            return "redirect:/csboard/list";
        }
    }

    // Delete a reply
    @DeleteMapping("/reply/delete/{id}")
    @ResponseBody
    public String deleteReply(@PathVariable Long id,
                             @RequestParam("csBoardId") Long csBoardId,
                             @AuthenticationPrincipal PrincipalDetail principalDetail) {
        try {
            // Check if user is logged in
            if (principalDetail == null) {
                return "unauthorized";
            }

            UserEntity currentUser = principalDetail.getUser();

            // Check if user is an admin
            boolean isAdmin = "ROLE_ADMIN".equals(currentUser.getRole());

            if (!isAdmin) {
                log.warn("Unauthorized reply deletion attempt by: " + currentUser.getUsername());
                return "unauthorized";
            }

            csBoardReplyService.deleteReply(id);
            return "success";
        } catch (Exception e) {
            log.error("Error deleting reply: " + e.getMessage());
            return "error";
        }
    }
}
