package com.azentrix.budgettracker.service;

import com.azentrix.budgettracker.entity.User;

public interface UserService {

    User register(String username, String email, String password);

    User login(String username, String password);
}