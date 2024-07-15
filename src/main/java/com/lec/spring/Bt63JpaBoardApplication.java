package com.lec.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing  // 스프링의 기본 Listener 작동시키기
public class Bt63JpaBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(Bt63JpaBoardApplication.class, args);
    }

}
