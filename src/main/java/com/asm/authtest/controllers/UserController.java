package com.asm.authtest.controllers;

import com.asm.authtest.models.User;
import com.asm.authtest.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(){
        List<User> users = userService.getAll();
        return ResponseEntity.ok().body(users);
    }
}
