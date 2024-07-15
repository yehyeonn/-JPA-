package com.lec.spring.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder    // builder pattern 사용 가능
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity(name = "t8_post")
@DynamicInsert  // insert 시 null 인 필드는 제외 시킨다
@DynamicUpdate  // update 시 null 인 필드는 제외 시킨다
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subject;     // 제목

    @Column(columnDefinition = "LONGTEXT")
    private String content;     // 내용

    @ColumnDefault(value = "0")
    @Column(insertable = false)
    private Long viewCnt;   // 조회수

    // POST:USER = N:1
    @ManyToOne(optional = false)    // 작성자가 null 일 경우가 없기 때문
    // optional = false : user_id bigint not null
    //  이게 없으면 Post 의 find..동작시  Post 와 User 사이에 left outer join 발생
    @ToString.Exclude
    private User user;  // 글 작성자 (FK)

    // 첨부파일
    // Post:File = 1:N
    // cascade = CascadeType.ALL  : 삭제등의 동작 발생시 child 도 함께 삭제
    @OneToMany(cascade = CascadeType.ALL)   // 부모가 삭제 될 때 자식도 삭제
    @JoinColumn(name = "post_id")   // attachment에 post_id 라는 외래키 생성(이상한 중간 테이블 생성 X)
    @ToString.Exclude
    @Builder.Default    // builder 제공 안 함
    private List<Attachment> fileList = new ArrayList<>();

    public void addFiles(Attachment... files){
        Collections.addAll(fileList, files);
    }

    // 댓글
    // Post:Comment = 1:N
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    @ToString.Exclude
    @Builder.Default    // 이거 뭐였지...
    private List<Comment> commentList = new ArrayList<>();

    public void addComments(Comment... comments){
        Collections.addAll(commentList, comments);
    }





}
