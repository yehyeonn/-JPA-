package com.lec.spring.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "t8_attachment")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id")
    private Long post;  // FK

    @Column(nullable = false)
    private String sourcename;  // 원본 파일명

    @Column(nullable = false)
    private String filename;    // 저장된 파일명(rename 된 파일명)

    @Transient  // DB 생성 X
    private boolean isImage; // 이미지 파일인지 여부(기본적으로 false)
}