package com.asm.authtest.services;

import com.asm.authtest.models.User;
import com.asm.authtest.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    //private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<User> userOp = userRepository.findByUsername(userName);

        if(userOp.isEmpty())
        {
            String msg = MessageFormat.format("user {} not found in database", userName);
            log.error(msg);
            throw new UsernameNotFoundException(msg);
        }
        User user = userOp.get();
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), authorities);
    }

    public UserServiceImpl(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAll()
    {
        return userRepository.getAll() ;
    }

    @Override
    public Optional<User> getUser(String userName) {
        log.info("findByUsername {}", userName);
        return userRepository.findByUsername(userName) ;
    }

    @Override
    public User saveUser(User user) {
        log.info("saving new user {} ", user.getUserName());
        //user.setPassword(passwordEncoder.encode(user.getPassword()));
        return user;
    }
}
