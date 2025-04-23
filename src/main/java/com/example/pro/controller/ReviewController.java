package com.example.pro.controller;


import com.example.pro.dto.ReviewDTO;
import com.example.pro.service.ReviewService;
import com.example.pro.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Log4j2
@RequestMapping("/view")
public class ReviewController {
    @Autowired
    private UserService userService;
    @Autowired
    private ReviewService reviewService;

    @GetMapping("/read/{recipe_id}")
    public String writer(@PathVariable Long recipe_id, Model model) {
       return "view/reviewregister";
    }
    @PostMapping("/reviewregister")
    public String addReview(@ModelAttribute ReviewDTO reviewDTO) {

        reviewService.registerReview(reviewDTO);

        return "redirect:/read";  
    }


}
