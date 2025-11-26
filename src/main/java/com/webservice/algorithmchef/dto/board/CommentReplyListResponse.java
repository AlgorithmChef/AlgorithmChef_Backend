package com.webservice.algorithmchef.dto.board;

import com.webservice.algorithmchef.dto.board.BoardPostResponse.CommentSimple;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentReplyListResponse {
    private List<CommentSimple> replies;
    private PageInfo pageInfo;
}
