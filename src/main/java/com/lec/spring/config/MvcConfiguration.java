// static 이 아닌 다른 상대경로 기준으로 사진을 넣을 수 있다.
package com.lec.spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfiguration {

    @Bean
    public PasswordEncoder encoder() {           // 암호화는 되지만 복호화는 불가능
        return new BCryptPasswordEncoder();
    }

    @Configuration
    public static class LocalMvcConfiguration implements WebMvcConfigurer {
        // 이너 클래스가 WebMvcConfigurer 구현 => Spring WebMvcConfiguration 관련 세팅 변경 가능, 기본은 static 폴더에서 사용

        // 파일 업로드
        // resource 경로 추가(overriding 사용)
        @Value("${app.upload.path}")
        private String uploadDir;

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            System.out.println("LocalMvcConfiguration.addResourceHandlers() 호출");   // 이건 처음 SpringBoot 가동 시에 호출됨
            //  /upload/** URL 로 request 가 들어오면
            // upload/ 경로의 resource 가 동작케 함. (이걸 지금 여기서 만드는 군요)
            // IntelliJ 의 경우 이 경로를 module 이 아닌 project 이하에 생성해야 한다.
            registry
                    .addResourceHandler("/upload/**")
                    .addResourceLocations("file:" + uploadDir + "/");
        }   // 이건 static 경로를 하나 더 추가하는 것. => 파일에 접근할 수 있음
    }
}
