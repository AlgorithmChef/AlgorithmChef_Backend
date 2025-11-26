package com.webservice.algorithmchef.dto.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardPostRequest {

    // 게시글 카테고리 (나눔, 질문 등)
    private String category;

    // 게시글 제목
    private String title;

    // 게시글 내용 (DB에서는 BLOB이지만, JSON 전송 시 String으로 처리)
    private String content;

    // 참고: user_id는 보통 Controller에서 @AuthenticationPrincipal을 통해 주입받으므로 DTO에서 제외합니다.
}
