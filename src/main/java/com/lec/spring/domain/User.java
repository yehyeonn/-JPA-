package com.lec.spring.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity(name = "t8_user")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;    // 아이디
    @JsonIgnore // json 에서 안 보이게
    private String password;    // 비밀번호

    @Transient  // Entity 에 반영 안 함.(검증 용도이기 때문)
    @ToString.Exclude   // ToString 에서 제외
    @JsonIgnore
    private String re_password; // 비밀번호 확인 입력, DB로 저장은 안 하고 html 폼에서 비밀번호 일치 확인 여부를 위해 필요

    @Column(nullable = false)
    private String name;     // 회원이름
    private String email;   // 이메일

    // fetch 기본값
    // @OneToMany, ManyToMany -> Fetch.Lazy
    // @OneToOne, ManyToOne -> Fetch.EAGER
    @ManyToMany(fetch = FetchType.EAGER)
    @ToString.Exclude
    @Builder.Default    // 안 하면 NPE
    @JsonIgnore
    private List<Authority> authorities = new ArrayList<>();

    // 만들어두면 좋다(릴레이션 만들 때 도우미 메소드)
    public void addAuthority(Authority... authorities) {
        Collections.addAll(this.authorities, authorities);
    }

    // OAuth2 Client => 카카오톡으로 로그인한 계정 정보를 가져와 내 사이트에 저장해야함(DB에도 필요!)
    private String provider;
    private String providerId;


}
