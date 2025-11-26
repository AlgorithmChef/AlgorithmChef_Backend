package com.webservice.algorithmchef.repository;

import com.webservice.algorithmchef.model.BoardPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardPostRepository extends JpaRepository<BoardPost, Long> {

    /**
     * 게시글 전체 목록을 작성일 기준 내림차순(최신순)으로 조회합니다.
     * 사용처: 게시판 메인 화면
     */
    List<BoardPost> findAllByOrderByCreatedAtDesc();

    /**
     * 특정 카테고리의 게시글 목록을 작성일 기준 내림차순으로 조회합니다.
     * 사용처: 카테고리별 필터링 (나눔, 질문 등)
     */
    List<BoardPost> findByCategoryOrderByCreatedAtDesc(String category);

    /**
     * 특정 사용자가 작성한 게시글 목록을 조회합니다.
     * 사용처: 마이페이지 - 내가 쓴 글
     */
    List<BoardPost> findByUser_IdOrderByCreatedAtDesc(Long userId);
}
