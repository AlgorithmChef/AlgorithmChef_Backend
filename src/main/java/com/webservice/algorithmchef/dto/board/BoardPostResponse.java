package com.webservice.algorithmchef.dto.board;

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
    private Long userId;        // 작성자 식별자
    private String writerName;  // 작성자 닉네임/이름 (User 테이블 조인 필요 시)
    private String category;
    private String title;
    private String content;     // BLOB -> String 변환된 내용
    private LocalDateTime createdAt;
    private LocalDateTime modifiedDate;

    // 해당 게시글에 달린 댓글 목록 (선택사항: 상세 조회 시 같이 내려줄 경우 필요)
    private List<BoardCommentResponse> comments;

    // Entity -> DTO 변환 편의 메서드
    public static BoardPostResponse from(BoardPost post, String writerName, List<BoardCommentResponse> comments) {
        return BoardPostResponse.builder()
                .postId(post.getPostId())
                .userId(post.getUser().getId()) // User 객체에서 ID 추출 가정
                .writerName(writerName)
                .category(post.getCategory())
                .title(post.getTitle())
                .content(post.getContent()) // post.getContent()가 String을 반환한다고 가정
                .createdAt(post.getCreatedAt())
                .modifiedDate(post.getModifiedDate())
                .comments(comments)
                .build();
    }
}
