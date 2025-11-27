package com.webservice.algorithmchef.repository;

import com.webservice.algorithmchef.model.BoardPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardPostRepository extends JpaRepository<BoardPost, Long> {

    @EntityGraph(attributePaths = {"user"})
    Page<BoardPost> findAll(Pageable pageable);

    // 카테고리별 조회
    @EntityGraph(attributePaths = {"user"})
    Page<BoardPost> findByCategory(String category, Pageable pageable);
}
