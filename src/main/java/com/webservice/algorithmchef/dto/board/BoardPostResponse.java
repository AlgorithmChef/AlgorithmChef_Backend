package com.webservice.algorithmchef.dto.board;

import com.webservice.algorithmchef.model.BoardComment;
import com.webservice.algorithmchef.model.BoardPost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardPostResponse {
    private Long postId;
    private String userId;
    private String category;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private List<CommentSimple> comments;
    private PageInfo pageInfo;

    @Getter
    @Builder
    public static class CommentSimple {
        private Long commentId;
        private String userId;
        private String content;
        private LocalDateTime createdAt;
        private boolean isDeleted;
        private int replyCount;

        public static CommentSimple from(BoardComment comment) {
            // 삭제된 댓글 처리
            String displayContent = comment.isDeleted() ? "삭제된 댓글입니다." : comment.getContent();
            // 유저 처리 (삭제된 유저일 경우 로직 추가 가능, 여기선 기본 처리)
            String displayUserId = comment.getUser() != null ? comment.getUser().getUserId() : "(삭제된 사용자)";

            return CommentSimple.builder()
                    .commentId(comment.getCommentId())
                    .userId(displayUserId)
                    .content(displayContent)
                    .createdAt(comment.getCreatedAt())
                    .isDeleted(comment.isDeleted())
                    // 대댓글 개수 (Children 리스트 사이즈)
                    .replyCount(comment.getChildren().size())
                    .build();
        }
    }

    public static BoardPostResponse of(BoardPost post, List<CommentSimple> comments, PageInfo pageInfo) {
        return BoardPostResponse.builder()
                .postId(post.getPostId())
                .userId(post.getUser().getUserId())
                .category(post.getCategory())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .comments(comments)
                .pageInfo(pageInfo)
                .build();
    }
}
