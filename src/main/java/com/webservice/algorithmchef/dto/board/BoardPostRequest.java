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

}
