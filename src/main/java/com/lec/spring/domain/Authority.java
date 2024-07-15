package com.lec.spring.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 권한 부여
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "t8_authority")
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // PK

    @Column(length = 40, nullable = false, unique = true)
    private String name;    // 권한명  ex) "ROLE_MEMBER", "ROLE_ADMIN"
}
