package com.lec.spring.repository;

import com.lec.spring.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repository layer(aka. Data layer)
// DataSource(DB) 등에 대한 직접적인 접근 => DB에 접근하게는 해줘야함!
// 어떤 동작이 필요한지 설계하는 Interface(메소드 선언)
public interface PostRepository extends JpaRepository<Post, Long> {

}
