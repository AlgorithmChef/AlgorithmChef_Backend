package com.webservice.algorithmchef.repository;

import com.webservice.algorithmchef.model.BoardComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {

    // 해당 postId에 달린 모든 댓글 조회
    List<BoardComment> findByPost_PostIdOrderByCreatedAtAsc(Long postId);

    // 최상위 부모 댓글만 조회
    Page<BoardComment> findByPost_PostIdAndParentCommentIsNull(Long postId, Pageable pageable);

    /**
     * (성능 최적화용) 게시글의 댓글을 가져올 때 작성자(User) 정보까지 한 번에 가져옵니다 (Fetch Join).
     * N+1 문제를 방지하여 조회 성능을 높입니다.
     */
    @Query("SELECT c FROM BoardComment c JOIN FETCH c.user WHERE c.post.postId = :postId ORDER BY c.createdAt ASC")
    List<BoardComment> findCommentsByPostIdWithUser(@Param("postId") Long postId);

    // 대댓글 페이징 및 조회
    Page<BoardComment> findByParentComment_CommentId(Long parentCommentId, Pageable pageable);
}
