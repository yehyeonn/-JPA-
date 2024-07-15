package com.lec.spring.service;

// Service layer
// - Business logic, Transaction 담당
// - Controller 와 Data 레이어의 분리

import com.lec.spring.domain.Post;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface BoardService { // 3. 넘어와서 Post post 에 넘겨줌 이때 아이디 값 생김

    // 글작성
    int write(Post post, Map<String, MultipartFile> files);

    // 특정 id 의 글 조회
    // 트랜잭션 처리
    // 1. 조회수 증가 (UPDATE)
    // 2. 글 읽어오기 (SELECT)
    @Transactional  // 처음 성공 => 두번째 실패하면 자동으로 전부 빠꾸
    Post detail(Long id);   // 특정 id를 받아서 Post 리턴

    // 글 목록 보기
    List<Post> list();

    // 페이징 리스트 메소드 overriding(Impl)
    List<Post> list(Integer page, Model model);

    // 수정하기(어렵다...)
    // 특정 id 의 글 읽어오기 (SELECT)
    // 조회수 증가 없음!!
    Post selectById(Long id);

    // 특정 id 글 수정하기 (제목, 내용)  (UPDATE)
    int update(Post post, Map<String, MultipartFile> files, Long[] delfile);    // 새로 추가하는 파일과 삭제하는 파일 정보 추가

    // 특정 id를 삭제(detail 창에서부터 시작됨)
    int deleteByID(Long id);


}
