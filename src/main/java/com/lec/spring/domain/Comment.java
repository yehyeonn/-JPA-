package com.lec.spring.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    private Long id;    // PK

    @ToString.Exclude
    private User user; // 댓글 작성자 정보(FK)

    @JsonIgnore // Json 변환 시에 이건 빼주세요~
    private Long post_id;   // 어느 글의 댓글인지(FK)

    private String content; // 댓글 내용

    // java.time.* 객체 변환을 위한 annotation
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @JsonProperty("regdate")    // 필드명과 다르게 하고 싶을 때 사용 변환하고자 하는 형태 지정 가능
    private LocalDateTime regDate;  // 댓글 등록 시간

}
