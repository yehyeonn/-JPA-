package com.lec.spring.repository;

import com.lec.spring.domain.Authority;
import com.lec.spring.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Authority findByName(String name);

}
