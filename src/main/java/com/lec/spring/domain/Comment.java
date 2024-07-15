package com.lec.spring.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "t8_comment")
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // PK

    // Comment:User = N:1   특정 댓글의 -> 작성자 정보 필요.
    @ManyToOne(optional = false)
    @ToString.Exclude
    private User user; // 댓글 작성자 정보(FK)

    @Column(name = "post_id")
    @JsonIgnore // Json 변환 시에 이건 빼주세요~
    private Long post;   // 어느 글의 댓글인지(FK)

    @Column(nullable = false)
    private String content; // 댓글 내용

}
