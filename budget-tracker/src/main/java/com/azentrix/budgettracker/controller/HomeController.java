package com.azentrix.budgettracker.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session) {
        // If not logged in, go to login page
        if (session.getAttribute("userId") == null) {
            return "redirect:/login.html";
        }
        return "redirect:/index.html";
    }
}