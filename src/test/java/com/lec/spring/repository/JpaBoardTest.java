package com.lec.spring.repository;

import com.lec.spring.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootTest
public class JpaBoardTest {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    public void init() {
        System.out.println("[init]");

        // Authotity 생성
        System.out.println("\nAuthority 생성 " + "-".repeat(20));
        Authority auth_member = Authority.builder()
                .name("ROLE_MEMBER")
                .build();
        Authority auth_admin = Authority.builder()
                .name("ROLE_ADMIN")
                .build();

        authorityRepository.saveAndFlush(auth_member);  // INSERT
        authorityRepository.saveAndFlush(auth_admin);   // INSERT

        authorityRepository.findAll().forEach(System.out::println); // SELECT

        // User 생성
        System.out.println("\nUser 생성 " + "-".repeat(20));

        User user1 = User.builder()
                .username("USER1")
                .password(passwordEncoder.encode("1234"))
                .name("회원1")
                .email("user1@mail.com")
                .build();

        User user2 = User.builder()
                .username("USER2")
                .password(passwordEncoder.encode("1234"))
                .name("회원2")
                .email("user2@mail.com")
                .build();

        User admin1 = User.builder()
                .username("ADMIN1")
                .password(passwordEncoder.encode("1234"))
                .name("관리자1")
                .email("admin@mail.com")
                .build();

        user1.addAuthority(auth_member);
        admin1.addAuthority(auth_member, auth_admin);


        userRepository.saveAll(List.of(user1, user2, admin1));
        userRepository.findAll().forEach(System.out::println);

        // 특정 User 권한 조회
        System.out.println("\nUser 및 권한 조회" + "-".repeat(20));
        user1 = userRepository.findById(1L).orElse(null);       // Eager Fetch. User 을 읽어올 때 Authority 까지 불러온다
        System.out.println(user1.getAuthorities());

        // 글 Post 작성
        System.out.println("\nPost 작성" + "-".repeat(20));
        Post p1 = Post.builder()
                .subject("제목입니다1")
                .content("내용입니다1")
                .user(user1)  // FK, Entity 주입
                .build();

        Post p2 = Post.builder()
                .subject("제목입니다2")
                .content("내용입니다2")
                .user(user1)
                .build();

        Post p3 = Post.builder()
                .subject("제목입니다3")
                .content("내용입니다3")
                .user(admin1)
                .build();

        Post p4 = Post.builder()
                .subject("제목입니다4")
                .content("내용입니다4")
                .user(admin1)
                .build();

        postRepository.saveAll(List.of(p1, p2, p3, p4));
        postRepository.findAll().forEach(System.out::println);

        // 글 Post 동작
        System.out.println("\nPost 동작" + "-".repeat(20));
        {
            Post post = postRepository.findById(1L).orElse(null);
            System.out.println(post);
            System.out.println(post.getUser());
            System.out.println(post.getUser().getAuthorities());    // fetch 해서 가능
        }

        // 첨부파일 추가
        System.out.println("\n첨부파일 추가" + "-".repeat(20));


        Attachment attachment1 = Attachment.builder()
                .filename("face01.png")
                .sourcename("face01.png")
                .post(p1.getId())   // FK, Entity 아닌 직접 ID 값 주입
                .build();

        Attachment attachment2 = Attachment.builder()
                .filename("face02.png")
                .sourcename("face02.png")
                .post(p1.getId())
                .build();

        Attachment attachment3 = Attachment.builder()
                .filename("face03.png")
                .sourcename("face03.png")
                .post(p2.getId())
                .build();

        Attachment attachment4 = Attachment.builder()
                .filename("face04.png")
                .sourcename("face04.png")
                .post(p2.getId())
                .build();

        Attachment attachment5 = Attachment.builder()
                .filename("face05.png")
                .sourcename("face05.png")
                .post(p3.getId())
                .build();

        Attachment attachment6 = Attachment.builder()
                .filename("face06.png")
                .sourcename("face06.png")
                .post(p3.getId())
                .build();

        Attachment attachment7 = Attachment.builder()
                .filename("face07.png")
                .sourcename("face07.png")
                .post(p4.getId())
                .build();

        Attachment attachment8 = Attachment.builder()
                .filename("face08.png")
                .sourcename("face08.png")
                .post(p4.getId())
                .build();

        attachmentRepository.saveAll(List.of(attachment1, attachment2, attachment3, attachment4, attachment5, attachment6, attachment7, attachment8));
        attachmentRepository.findAll().forEach(System.out::println);

        // 첨부파일 Attachment 동작
        // 특정 글 의 첨부파일
        System.out.println("\n첨부파일 Attachment 동작" + "-".repeat(20));
        {
            Long postId = 1L;

            var result = attachmentRepository.findByPost(postId);

            System.out.println(postId + "번 글의 첨부파일들: " + result.size() + "개");
            System.out.println(result);
        }

        // 댓글 Comment
        System.out.println("\n댓글 생성 " + "-".repeat(30));
        Comment c1 = Comment.builder()
                .content("1. user1이 1번글에 댓글 작성.")
                .user(user1)  // FK(ManyToOne 이라 Entity)
                .post(p1.getId())  // FK(Long 타입이라 Id)
                .build();
        Comment c2 = Comment.builder()
                .content("2. user1이 1번글에 댓글 작성.")
                .user(user1)
                .post(p1.getId())
                .build();
        Comment c3 = Comment.builder()
                .content("3. user1이 2번글에 댓글 작성.")
                .user(user1)
                .post(p2.getId())
                .build();
        Comment c4 = Comment.builder()
                .content("4. user1이 2번글에 댓글 작성.")
                .user(user1)
                .post(p2.getId())
                .build();
        Comment c5 = Comment.builder()
                .content("5. user1이 3번글에 댓글 작성.")
                .user(user1)
                .post(p3.getId())
                .build();
        Comment c6 = Comment.builder()
                .content("6. user1이 3번글에 댓글 작성.")
                .user(user1)
                .post(p3.getId())
                .build();
        Comment c7 = Comment.builder()
                .content("7. user1이 4번글에 댓글 작성.")
                .user(user1)
                .post(p4.getId())
                .build();
        Comment c8 = Comment.builder()
                .content("8. user1이 4번글에 댓글 작성.")
                .user(user1)
                .post(p4.getId())
                .build();
        Comment c9 = Comment.builder()
                .content("9. admin1이 1번글에 댓글 작성.")
                .user(admin1)
                .post(p1.getId())
                .build();
        Comment c10 = Comment.builder()
                .content("10. admin1이 1번글에 댓글 작성.")
                .user(admin1)
                .post(p1.getId())
                .build();
        Comment c11 = Comment.builder()
                .content("11. admin1이 2번글에 댓글 작성.")
                .user(admin1)
                .post(p2.getId())
                .build();
        Comment c12 = Comment.builder()
                .content("12. admin1이 2번글에 댓글 작성.")
                .user(admin1)
                .post(p2.getId())
                .build();
        Comment c13 = Comment.builder()
                .content("13. admin1이 3번글에 댓글 작성.")
                .user(admin1)
                .post(p3.getId())
                .build();
        Comment c14 = Comment.builder()
                .content("14. admin1이 3번글에 댓글 작성.")
                .user(admin1)
                .post(p3.getId())
                .build();
        Comment c15 = Comment.builder()
                .content("15. admin1이 4번글에 댓글 작성.")
                .user(admin1)
                .post(p4.getId())
                .build();
        Comment c16 = Comment.builder()
                .content("16. admin1이 4번글에 댓글 작성.")
                .user(admin1)
                .post(p4.getId())
                .build();


        commentRepository.saveAll(List.of(
                c1, c2, c3, c4,
                c5, c6, c7, c8,
                c9, c10, c11, c12,
                c13, c14, c15, c16
        ));


        commentRepository.findAll().forEach(System.out::println);

        System.out.println("\n특정 댓글의 User 정보 조회" + "-".repeat(30));
        {
            Comment comment = commentRepository.findById(1L).orElse(null);
            System.out.println(comment.getUser());  // user1
            System.out.println(comment.getUser().getAuthorities()); // ROLE_MEMBER

        }


        System.out.println("\n특정 글의 댓글목록 조회" + "-".repeat(30));
        {   // Lazy fetch 이기 때문에 바로 comment 를 가져오지 못함
            Post post = postRepository.findById(1L).orElse(null);
            var result = commentRepository.findByPost(post.getId(), Sort.by(Sort.Order.desc("id")));
            System.out.println(result);
        }

    }
}
