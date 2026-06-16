package com.azentrix.budgettracker.service;

import com.azentrix.budgettracker.entity.User;
import com.azentrix.budgettracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public User register(String username, String email, String password) {

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already taken!");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered!");
        }

        // Hash the password before saving
        String hashedPassword = encoder.encode(password);
        User user = new User(username, hashedPassword, email);
        return userRepository.save(user);
    }

    @Override
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password!"));

        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password!");
        }
        return user;
    }
}