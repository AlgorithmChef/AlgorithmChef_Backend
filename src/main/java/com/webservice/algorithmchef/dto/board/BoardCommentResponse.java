package com.webservice.algorithmchef.dto.board;

import com.webservice.algorithmchef.model.BoardComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardCommentResponse {

    private Long commentId;
    private Long postId;
    private Long userId;       // 댓글 작성자 ID
    private String writerName; // 댓글 작성자 이름
    private Long parentCommentId; // 부모 댓글 ID (없으면 null)
    private int depth;         // 대댓글 깊이 (0: 원댓글, 1: 대댓글 ...)
    private String content;    // "삭제된 댓글입니다" 처리를 위해 필요
    private LocalDateTime createdAt;
    private LocalDateTime modifiedDate;
    private boolean isDeleted; // 프론트에서 "삭제됨" 표시 여부 판단용

    // 자식 댓글(대댓글) 목록 - 계층형 구조 표현
    @Builder.Default
    private List<BoardCommentResponse> children = new ArrayList<>();

    // Entity -> DTO 변환 편의 메서드
    public static BoardCommentResponse from(BoardComment comment, String writerName) {
        return BoardCommentResponse.builder()
                .commentId(comment.getCommentId())
                .postId(comment.getPost().getPostId())
                .userId(comment.getUser().getId())
                .writerName(writerName)
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null)
                .depth(comment.getDepth())
                // 삭제된 댓글일 경우 내용을 변경해서 보낼 수도 있고, isDeleted 플래그만 보낼 수도 있음
                .content(comment.isDeleted() ? "삭제된 댓글입니다." : comment.getContent())
                .createdAt(comment.getCreatedAt())
                .modifiedDate(comment.getModifiedDate())
                .isDeleted(comment.isDeleted())
                .build();
    }
}
