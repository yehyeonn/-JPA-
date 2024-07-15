package com.lec.spring.config.oauth.provider;

// 페이스북이나 구글이나.. attribute 값이 다 달라서 객체지향적인 면에서 유지보수가 쉽기 위해 만든 인터페이스
public interface OAuth2UserInfo {
    String getProvider();  // "google", "facebook" ...
    String getProviderId(); // PK값
    String getEmail();
    String getName();
}
