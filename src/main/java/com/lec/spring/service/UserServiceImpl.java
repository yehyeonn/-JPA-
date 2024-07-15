package com.lec.spring.service;

import com.lec.spring.domain.Authority;
import com.lec.spring.domain.User;
import com.lec.spring.repository.AuthorityRepository;
import com.lec.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;


    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Autowired
    public void setAuthorityRepository(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;    // 암호화

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username.toUpperCase());
    }

    @Override
    public boolean isExist(String username) {
        User user = findByUsername(username);
        return (user != null);
    }

    @Override
    public int register(User user) {
        user.setUsername(user.getUsername().toUpperCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);

        // 신규 회원에 ROLE_MEMBER 권한 부여
        Authority auth = authorityRepository.findByName("ROLE_MEMBER");
        user.addAuthority(auth);
        userRepository.save(user);
        return 1;
    }

    @Override
    public List<Authority> selectAuthoritiesById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if(user != null) return user.getAuthorities();
        return new ArrayList<>();   // null 일 경우 대비
    }
}
