package com.webservice.algorithmchef.controller;

import com.webservice.algorithmchef.dto.board.BoardCommentRequest;
import com.webservice.algorithmchef.dto.board.BoardPostResponse;
import com.webservice.algorithmchef.dto.board.BoardPostListResponse;
import com.webservice.algorithmchef.dto.board.BoardPostRequest;
import com.webservice.algorithmchef.dto.board.CommentReplyListResponse;
import com.webservice.algorithmchef.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /**
     * 1. 게시글 목록 조회
     * GET /board/posts
     */
    @GetMapping("/posts")
    public ResponseEntity<BoardPostListResponse> getPostList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) String filter
    ) {
        BoardPostListResponse response = boardService.getPostList(page, size, sort, filter);
        return ResponseEntity.ok(response);
    }

    /**
     * 2. 새 게시글 작성
     * POST /board/post
     */
    @PostMapping("/post")
    public ResponseEntity<Map<String, String>> createPost(
            @RequestBody BoardPostRequest requestDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boardService.createPost(requestDto, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "새 게시글 작성완료 되었습니다."));
    }

    /**
     * 3. 게시글 상세 조회
     * GET /board/post/{postId}
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<BoardPostResponse> getPostDetail(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,asc") String sort
    ) {
        try {
            BoardPostResponse response = boardService.getPostDetail(postId, page, size, sort);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 6. 댓글 작성
     * POST /board/post/{postId}/comment
     */
    @PostMapping("/post/{postId}/comment")
    public ResponseEntity<Map<String, String>> createComment(
            @PathVariable Long postId,
            @RequestBody BoardCommentRequest requestDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            boardService.createComment(postId, requestDto, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "댓글 작성완료"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 5. 대댓글 조회
     * GET /board/comments/{commentId}/replies
     */
    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<CommentReplyListResponse> getReplies(
            @PathVariable Long commentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,asc") String sort
    ) {
        try {
            CommentReplyListResponse response = boardService.getReplies(commentId, page, size, sort);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}