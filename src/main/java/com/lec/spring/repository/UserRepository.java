package com.lec.spring.repository;

import com.lec.spring.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

// 데이터 속성 직접 다루는 객체
public interface UserRepository extends JpaRepository<User, Long> {

    // 사용자의 username 으로 조회
    User findByUsername(String username);
}
