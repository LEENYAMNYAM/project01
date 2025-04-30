package com.example.pro.controller;

import com.example.pro.dto.CartDTO;
import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cart")
public class CartController {

    @GetMapping("/list")
    public String list(Model model) {

    }



    @GetMapping("/checkout")
    public String checkout(Model model) {
        // 임시로 빈 카트 객체 추가
        model.addAttribute("cart", new CartDTO());  // 빈 CartDTO 추가

        return "/cart/checkout";  // checkout.html로 이동
    }

}
