package com.lec.spring.service;

import com.lec.spring.domain.QryCommentList;
import com.lec.spring.domain.QryResult;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    //TODO
    @Override
    public QryCommentList list(Long postId) {
        return null;
    }

    @Override
    public QryResult write(Long postId, Long userId, String content) {
        return null;
    }

    @Override
    public QryResult delete(Long id) {
        return null;
    }
}
