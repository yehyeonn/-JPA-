package com.lec.spring.service;

import com.lec.spring.domain.Authority;
import com.lec.spring.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;    // 암호화

    //TODO
    @Override
    public User findByUsername(String username) {
        return null;
    }

    @Override
    public boolean isExist(String username) {
        return false;
    }

    @Override
    public int register(User user) {
        return 0;
    }

    @Override
    public List<Authority> selectAuthoritiesById(Long id) {
        return null;
    }
}
