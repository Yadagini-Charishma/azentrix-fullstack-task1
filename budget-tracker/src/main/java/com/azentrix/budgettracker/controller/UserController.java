package com.azentrix.budgettracker.controller;

import com.azentrix.budgettracker.entity.User;
import com.azentrix.budgettracker.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // ── REGISTER ──────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            User user = userService.register(
                    body.get("username"),
                    body.get("email"),
                    body.get("password")
            );
            return ResponseEntity.ok(Map.of(
                    "message", "Registered successfully!",
                    "username", user.getUsername()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── LOGIN ─────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body, HttpSession session) {
        try {
            User user = userService.login(body.get("username"), body.get("password"));

            // Save user info in session
            session.setAttribute("userId",   user.getId());
            session.setAttribute("username", user.getUsername());

            return ResponseEntity.ok(Map.of(
                    "message",  "Login successful!",
                    "userId",   user.getId(),
                    "username", user.getUsername()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── LOGOUT ────────────────────────────────────────────
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    // ── CHECK SESSION (is user still logged in?) ──────────
    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Long userId   = (Long)   session.getAttribute("userId");
        String username = (String) session.getAttribute("username");

        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        }
        return ResponseEntity.ok(Map.of("userId", userId, "username", username));
    }
}