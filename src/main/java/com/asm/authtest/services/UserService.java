package com.asm.authtest.services;

import com.asm.authtest.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAll();
    Optional<User> getUser(String userName);
    User saveUser(User user);
}
