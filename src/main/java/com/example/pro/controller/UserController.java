package com.example.pro.controller;

import com.example.pro.config.auth.PrincipalDetail;
import com.example.pro.entity.UserEntity;

import com.example.pro.dto.UserDTO;

import com.example.pro.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Log4j2
@RequestMapping("/userinfo")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @GetMapping("/join")
    public void join() {

    }
    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserDTO userDTO, RedirectAttributes redirectAttributes) {
        log.info("/user/register POST 요청 - 회원가입 처리: {}", userDTO.getUsername());

        try {

            userService.registerUser(userDTO);

            redirectAttributes.addFlashAttribute("message", "회원가입이 성공적으로 완료되었습니다!");
            return "redirect:/userinfo/login";

        } catch (Exception e) {

            log.error("회원가입 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "회원가입 실패: " + e.getMessage());
            return "redirect:/userinfo/join";
        }
    }
    @GetMapping("/login")
    public void login() {

    }

    @GetMapping("/userlist")
    public String userInfo(Model model) {
        log.info("Starting user info retrieval...");


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Logged-in username: {}", username);


        UserEntity userEntity = userService.readUser(username);
        if (userEntity == null) {
            log.error("User not found for username: {}", username);
            return "error";
        }
        log.info("User entity retrieved: {}", userEntity);


        UserDTO userDTO = userService.entityToDto(userEntity);
        log.info("User DTO created: {}", userDTO);


        model.addAttribute("user", userDTO);
        log.info("User DTO added to model");


        return "userinfo/userlist";
    }
    @GetMapping("/userupdate")
    public String userUpdate(@AuthenticationPrincipal PrincipalDetail principal, Model model) {


        model.addAttribute("user", principal.getUser());
        return "userinfo/userupdate";
    }
    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") UserDTO userDTO,
                             @AuthenticationPrincipal PrincipalDetail principal) {

        String username = principal.getUsername();

        // 기존 시그니처에 맞게 호출
        userService.updateUser(username, userDTO);

        return "redirect:/userinfo/userlist";
    }




    @GetMapping("/delete")
    public String deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        userService.deleteUser(username);

        // 로그아웃 처리할 수도 있음 (선택사항)
        SecurityContextHolder.clearContext();

        return "redirect:/logout"; // 혹은 메인 페이지로 리디렉트
    }


}
