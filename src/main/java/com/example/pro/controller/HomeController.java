package com.example.pro.controller;

import com.example.pro.dto.NoticeDTO;
import com.example.pro.dto.RecipeDTO;
import com.example.pro.service.NoticeService;
import com.example.pro.service.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class HomeController {

    private final RecipeService recipeService;
    private final NoticeService noticeService;

    @GetMapping("/")
    public String Home(Model model) {

    List<RecipeDTO> recipeDTOList = recipeService.getRecentRecipes();
    log.info("Home Controller RecipeDTOList: {}", recipeDTOList);

    if (recipeDTOList.isEmpty()) {
        model.addAttribute("message", "아직 등록된 레시피가 없습니다.");
    } else {
        model.addAttribute("recipeList", recipeDTOList);
    }

    List<NoticeDTO> latestNotices = noticeService.getLatest3Notices();
    log.info("Home Controller NoticeDTOList: {}", latestNotices);

    if (latestNotices == null || latestNotices.isEmpty()) {
        model.addAttribute("latestNotices", List.of());
    } else {
        model.addAttribute("latestNotices", latestNotices);
    }


        return "/home";
    }

}
