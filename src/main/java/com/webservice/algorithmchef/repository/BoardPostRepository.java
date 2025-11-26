package com.webservice.algorithmchef.repository;

import com.webservice.algorithmchef.model.BoardPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardPostRepository extends JpaRepository<BoardPost, Long> {
    // 카테고리 필터링 + 페이징
    Page<BoardPost> findByCategory(String category, Pageable pageable);

    // 전체 페이징 (필터 없을 때) - findAll(Pageable)은 JpaRepository에 기본 내장됨
}
