package com.example.pro.controller;

import com.example.pro.dto.RecipeDTO;
import com.example.pro.dto.NoticeDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        // 오늘의 추천 레시피 샘플
        RecipeDTO todayRecipe = RecipeDTO.builder()
                .title("엄마의 김치찌개")
                .mainImagePath("/images/sample_kimchi.jpg") // static/images/ 안에 샘플 이미지 필요
                .description("집밥의 기본, 얼큰한 김치찌개 레시피입니다.")
                .build();

        // 이달의 레시피 목록 샘플
        List<RecipeDTO> monthlyRecipes = new ArrayList<>();
        monthlyRecipes.add(RecipeDTO.builder()
                .title("된장찌개")
                .mainImagePath("/images/sample_dwenjang.jpg")
                .build());
        monthlyRecipes.add(RecipeDTO.builder()
                .title("비빔밥")
                .mainImagePath("/images/sample_bibimbap.jpg")
                .build());
        monthlyRecipes.add(RecipeDTO.builder()
                .title("김치볶음밥")
                .mainImagePath("/images/sample_kimchi_fried.jpg")
                .build());

        // 최신 공지사항 목록 샘플
        List<NoticeDTO> latestNotices = new ArrayList<>();
        latestNotices.add(NoticeDTO.builder()
                .id(1L)
                .title("5월 배송 휴무 안내")
                .build());
        latestNotices.add(NoticeDTO.builder()
                .id(2L)
                .title("신규 레시피 업데이트 안내")
                .build());
        latestNotices.add(NoticeDTO.builder()
                .id(3L)
                .title("이벤트 당첨자 발표")
                .build());

        model.addAttribute("todayRecipe", todayRecipe);
        model.addAttribute("monthlyRecipes", monthlyRecipes);
        model.addAttribute("latestNotices", latestNotices);

        return "home"; // 여기 절대 /home 아니야. 그냥 "home"
    }
}
