package com.asm.authtest.repositories;

import com.asm.authtest.models.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserRepository
{
    private final PasswordEncoder passwordEncoder;

    private List<User> users;

    public UserRepository(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        users = List.of(
                new User(1L, "angelo", "asm@google.com", passwordEncoder.encode("1234")),
                new User(2L, "teste", "teste@google.com", passwordEncoder.encode("1234")),
                new User(3L, "adm", "adm@google.com", passwordEncoder.encode("1234")),
                new User(4L, "unidade", "unidade@google.com", passwordEncoder.encode("1234"))
        );
    }

    public Optional<User> findByUsername(String userName)
    {
        return users.stream()
                .filter(usr -> usr.getUserName().equals(userName))
                .findFirst();
    }

    public List<User> getAll() {
        return users;
    }
}
