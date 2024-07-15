package com.lec.spring.domain.oauth;
// 직접 json -> java 객체 만들기

import lombok.Data;

@Data
public class KakaoOAuthToken {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private String expires_in;
    private String scope;
    private String refresh_token_expires_in;
}
