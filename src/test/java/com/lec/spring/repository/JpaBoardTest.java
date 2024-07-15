package com.lec.spring.repository;

import com.lec.spring.domain.Authority;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JpaBoardTest {
    @Test
    public void init() {
        System.out.println("[init]");

        // Authotity 생성
        System.out.println("\nAuthority 생성 " + "-".repeat(20));
        Authority auth_member = Authority.builder()
                .name("ROLE_MEMBER")
                .build();
        Authority auth_admin = Authority.builder()
                .name("ROLE_ADMIN")
                .build();




    }
}
