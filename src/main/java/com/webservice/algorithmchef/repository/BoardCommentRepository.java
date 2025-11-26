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

    /**
     * 특정 게시글(PostId)에 달린 모든 댓글을 조회합니다.
     * 대댓글 계층 구조를 서비스 로직에서 조립하기 위해,
     * 부모/자식 관계없이 해당 글의 모든 댓글을 작성일 순서(오래된 순)로 가져옵니다.
     *
     * @param postId 조회할 게시글의 ID
     * @return 해당 게시글의 댓글 리스트
     */
    List<BoardComment> findByPost_PostIdOrderByCreatedAtAsc(Long postId);

    /**
     * 특정 게시글의 '최상위 댓글(부모가 없는 댓글)'만 페이징 조회합니다.
     * 게시글 상세 조회 시 댓글 목록 페이징에 사용됩니다.
     */
    Page<BoardComment> findByPost_PostIdAndParentCommentIsNull(Long postId, Pageable pageable);

    /**
     * (성능 최적화용) 게시글의 댓글을 가져올 때 작성자(User) 정보까지 한 번에 가져옵니다 (Fetch Join).
     * N+1 문제를 방지하여 조회 성능을 높입니다.
     */
    @Query("SELECT c FROM BoardComment c JOIN FETCH c.user WHERE c.post.postId = :postId ORDER BY c.createdAt ASC")
    List<BoardComment> findCommentsByPostIdWithUser(@Param("postId") Long postId);

    /**
     * 특정 부모 댓글의 자식 댓글(대댓글)을 페이징하여 조회합니다.
     * 대댓글 더보기 기능 등에 사용됩니다.
     * * @param parentCommentId 부모 댓글 ID (CommentId)
     *
     * @param pageable 페이징 정보
     * @return 대댓글 Page 객체
     */
    Page<BoardComment> findByParentComment_CommentId(Long parentCommentId, Pageable pageable);
}
