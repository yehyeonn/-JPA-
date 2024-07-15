package com.lec.spring.service;

import com.lec.spring.domain.Comment;
import com.lec.spring.domain.QryCommentList;
import com.lec.spring.domain.QryResult;
import com.lec.spring.domain.User;
import com.lec.spring.repository.CommentRepository;
import com.lec.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private CommentRepository commentRepository;
    private UserRepository userRepository;

    @Autowired
    public void setCommentRepository(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public QryCommentList list(Long postId) {
        QryCommentList list = new QryCommentList();

        List<Comment> comments = commentRepository.findByPost(postId, Sort.by(Sort.Order.desc("id")));

        list.setCount(comments.size());
        list.setList(comments);
        list.setStatus("OK");

        return list;
    }

    @Override
    public QryResult write(Long postId, Long userId, String content) {
        User user = userRepository.findById(userId).orElse(null);

        Comment comment = Comment.builder()
                .user(user)
                .content(content)
                .post(postId)
                .build();

        commentRepository.save(comment);    // INSERT

        QryResult result = QryResult.builder()
                .count(1)
                .status("OK")
                .build();

        return result;
    }

    @Override
    public QryResult delete(Long id) {

        Comment comments = commentRepository.findById(id).orElse(null);
        int cnt = 0;
        String status = "FAIL";

        if(comments != null) {
            commentRepository.delete(comments);
            cnt = 1;
            status = "OK";
        }

        QryResult result = QryResult.builder()
                .count(cnt)
                .status(status)
                .build();

        return result;
    }
}
