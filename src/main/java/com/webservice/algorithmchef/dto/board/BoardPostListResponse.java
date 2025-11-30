package com.webservice.algorithmchef.dto.board;

import com.webservice.algorithmchef.model.BoardPost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 게시글 목록 반환 객체
public class BoardPostListResponse {
    // 목록 조회 시 반환할 Wrapper 클래스
    private List<BoardPostSimple> posts;
    private PageInfo pageInfo;

    @Getter
    @Builder
    public static class BoardPostSimple {
        private String title;
        private String userId;
        private String createdAt;
        private String category;
        private String content; // 목록 미리보기용
        private Long postId;

        public static BoardPostSimple from(BoardPost post) {
            return BoardPostSimple.builder()
                    .title(post.getTitle())
                    .postId(post.getPostId())
                    // User 엔티티의 userId (로그인 아이디) 사용
                    .userId(post.getUser().getUserId())
                    // 날짜 포맷팅 (yyyy-MM-dd)
                    .createdAt(post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .category(post.getCategory())
                    .content(post.getContent())
                    .build();
        }
    }
}
