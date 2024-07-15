package com.lec.spring.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String username;    // 아이디
    @JsonIgnore // json 에서 안 보이게
    private String password;    // 비밀번호

    @ToString.Exclude   // ToString 에서 제외
    @JsonIgnore
    private String re_password; // 비밀번호 확인 입력, DB로 저장은 안 하고 html 폼에서 비밀번호 일치 확인 여부를 위해 필요

    private String name;     // 회원이름
    private String email;   // 이메일

    @JsonIgnore
    private LocalDateTime regDate;

    // OAuth2 Client => 카카오톡으로 로그인한 계정 정보를 가져와 내 사이트에 저장해야함(DB에도 필요!)
    private String provider;
    private String providerId;


}
