package com.lec.spring.controller;

import com.lec.spring.domain.Comment;
import com.lec.spring.domain.QryCommentList;
import com.lec.spring.domain.QryResult;
import com.lec.spring.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController     // data 를 response 한다('View' 를 리턴하는 게 아니다!)
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/list/{postId}")
    public QryCommentList list(@PathVariable Long postId) {
        return commentService.list(postId);
    }

    @PostMapping("/write")
    public QryResult write(
            @RequestParam("post_id") Long postId,       // 클라이언트 측에서 "post_id" 라는 이름으로 넘어오는 값을 Long postId로 넣는다
            @RequestParam("user_id") Long userId,
            String content
    ){
        return commentService.write(postId, userId, content);
    }

    @PostMapping("/delete")
    public QryResult delete(Long id){
        return commentService.delete(id);
    }
}
