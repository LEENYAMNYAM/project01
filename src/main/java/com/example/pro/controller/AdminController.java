package com.example.pro.controller;

import com.example.pro.dto.IngredientDTO;
import com.example.pro.service.IngredientService;
import com.example.pro.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Log4j2
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private IngredientService ingredientService;
    @GetMapping("/adminlist")
    public String adminlist(Model model) {
        model.addAttribute("userList", userService.findAll());
        return "admin/adminlist";
    }

    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam String username) {
        userService.deleteUser(username);
        return "redirect:/admin/adminlist";
    }
    @GetMapping("/ingredientlist")
    public String getIngredientList(Model model) {
        List<IngredientDTO> ingredientDTOs = ingredientService.findAllIngredient();


        for (IngredientDTO ingredient : ingredientDTOs) {
            System.out.println("Ingredient Detail: " + ingredient.getDetail());  // 값 확인
        }



        model.addAttribute("ingredients", ingredientDTOs);
        return "admin/ingredientlist";
    }

    @GetMapping("/ingredientinsert")
    public String ingredientinsert(Model model) {
        return "admin/ingredientinsert";
    }
    @PostMapping("/ingredientinsert")
    public String ingredientinsert(IngredientDTO ingredientDTO) {
        ingredientService.saveIngredient(ingredientDTO);
        return "redirect:/admin/ingredientlist";
    }
    @GetMapping("/deleteIngredient/{id}")
    public String deleteIngredient(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        return "redirect:/admin/ingredientlist";
    }
    @GetMapping("/updateIngredient/{id}")
    public String updateIngredient(@PathVariable Long id, Model model) {
        IngredientDTO ingredientDTO = ingredientService.findIngredientById(id);
        model.addAttribute("ingredient", ingredientDTO);
        return "admin/updateIngredient";
    }
    @PostMapping("/updateIngredient/{id}")
    public String updateIngredient(@PathVariable Long id, IngredientDTO ingredientDTO) {
        ingredientService.updateIngredient(id, ingredientDTO);
        return "redirect:/admin/ingredientlist";
    }



}
