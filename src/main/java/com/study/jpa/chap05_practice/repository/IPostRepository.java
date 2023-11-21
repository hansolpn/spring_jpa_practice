package com.study.jpa.chap05_practice.repository;

import com.study.jpa.chap05_practice.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPostRepository
        extends JpaRepository<Post, Long> {
}
