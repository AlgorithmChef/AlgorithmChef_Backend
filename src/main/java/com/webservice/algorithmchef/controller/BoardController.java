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

    // 게시글 목록 조회(게시판)
    @GetMapping("/posts")
    public ResponseEntity<BoardPostListResponse> getPostList(
            @RequestParam(defaultValue = "0",value="page") int page,
            @RequestParam(defaultValue = "20",value="size") int size,
            @RequestParam(defaultValue = "createdAt,desc",value="sort") String sort,
            @RequestParam(required = false,value="filter") String filter
    ) {
        BoardPostListResponse response = boardService.getPostList(page, size, sort, filter);
        return ResponseEntity.ok(response);
    }

    // 게시글 작성
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
                .body(Map.of("message", "게시글이 작성되었습니다."));
    }

    // 게시글 조회
    @GetMapping("/post/{postId}")
    public ResponseEntity<BoardPostResponse> getPostDetail(
            @PathVariable("postId") Long postId,
            @RequestParam(defaultValue = "0",value="page") int page,
            @RequestParam(defaultValue = "20",value="size") int size,
            @RequestParam(defaultValue = "createdAt,asc",value="sort") String sort
    ) {
        try {
            BoardPostResponse response = boardService.getPostDetail(postId, page, size, sort);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 댓글 작성
    @PostMapping("/post/{postId}/comment")
    public ResponseEntity<Map<String, String>> createComment(
            @PathVariable("postId") Long postId,
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

    // 대댓글 조회
    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<CommentReplyListResponse> getReplies(
            @PathVariable("commentId") Long commentId,
            @RequestParam(defaultValue = "0",value="page") int page,
            @RequestParam(defaultValue = "10",value="size") int size,
            @RequestParam(defaultValue = "createdAt,asc",value="sort") String sort
    ) {
        try {
            CommentReplyListResponse response = boardService.getReplies(commentId, page, size, sort);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 게시글 수정
    @PutMapping("/post/{postId}")
    public ResponseEntity<Map<String, String>> updatePost(
            @PathVariable("postId") Long postId,
            @RequestBody BoardPostRequest requestDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            // 401 Unauthorized
        }

        try {
            // userDetails.getUsername()은 JWT에서 추출한 userId(String)
            boardService.updatePost(postId, requestDto, userDetails.getUsername());

            return ResponseEntity.ok(Map.of("message", "게시글이 수정되었습니다."));
        } catch (IllegalArgumentException e) {
            // 게시글이 없거나, 권한이 없는 경우
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("게시글 수정 오류", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "서버 오류가 발생했습니다."));
        }
    }

    // 게시글 삭제
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Map<String, String>> deletePost(
    		@PathVariable("postId") Long postId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            boardService.deletePost(postId, userDetails.getUsername());

            return ResponseEntity.ok(Map.of("message", "게시글이 삭제되었습니다."));
        } catch (IllegalArgumentException e) {
            // 게시글이 없거나, 권한이 없는 경우
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("게시글 삭제 오류", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "서버 오류가 발생했습니다."));
        }
    }
}