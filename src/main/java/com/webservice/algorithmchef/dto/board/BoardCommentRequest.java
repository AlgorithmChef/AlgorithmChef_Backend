package com.webservice.algorithmchef.dto.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardCommentRequest {

    // 댓글이 달릴 게시글 ID
    private Long postId;

    // 부모 댓글 ID (대댓글일 경우 필수, 최상위 댓글이면 null)
    private Long parentCommentId;

    // 댓글 내용
    private String content;
}
