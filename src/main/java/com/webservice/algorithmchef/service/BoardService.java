package com.webservice.algorithmchef.service;

import com.webservice.algorithmchef.dto.board.*;
import com.webservice.algorithmchef.model.BoardComment;
import com.webservice.algorithmchef.model.BoardPost;
import com.webservice.algorithmchef.model.User;
import com.webservice.algorithmchef.repository.BoardCommentRepository;
import com.webservice.algorithmchef.repository.BoardPostRepository;
import com.webservice.algorithmchef.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardPostRepository boardPostRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final UserRepository userRepository;

    // 게시글 목록 조회(게시판)
    @Transactional(readOnly = true)
    public BoardPostListResponse getPostList(int page, int size, String sortStr, String filter) {
        Pageable pageable = createPageable(page, size, sortStr);

        Page<BoardPost> postPage;
        if (filter != null && !filter.isEmpty()) {
            postPage = boardPostRepository.findByCategory(filter, pageable);
        } else {
            postPage = boardPostRepository.findAll(pageable);
        }

        List<BoardPostListResponse.BoardPostSimple> postDtos = postPage.getContent().stream()
                .map(BoardPostListResponse.BoardPostSimple::from)
                .collect(Collectors.toList());

        PageInfo pageInfo = PageInfo.from(postPage);

        return BoardPostListResponse.builder()
                .posts(postDtos)
                .pageInfo(pageInfo)
                .build();
    }

    // 게시글 작성
    @Transactional
    public void createPost(BoardPostRequest requestDto, String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다. userId=" + userId));

        BoardPost post = BoardPost.builder()
                .user(user)
                .category(requestDto.getCategory())
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .build();

        boardPostRepository.save(post);
    }

    // 게시글 조회
    @Transactional(readOnly = true)
    public BoardPostResponse getPostDetail(Long postId, int page, int size, String sortStr) {
        BoardPost post = boardPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. postId=" + postId));

        // 부모 댓글만 조회
        Pageable pageable = createPageable(page, size, sortStr);
        Page<BoardComment> commentPage = boardCommentRepository.findByPost_PostIdAndParentCommentIsNull(postId, pageable);

        List<BoardPostResponse.CommentSimple> commentDtos = commentPage.getContent().stream()
                .map(BoardPostResponse.CommentSimple::from)
                .collect(Collectors.toList());

        PageInfo pageInfo = PageInfo.from(commentPage);

        return BoardPostResponse.of(post, commentDtos, pageInfo);
    }

    // 댓글 작성
    @Transactional
    public void createComment(Long postId, BoardCommentRequest requestDto, String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다. userId=" + userId));

        BoardPost post = boardPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. postId=" + postId));

        BoardComment parentComment = null;
        int depth = 0;

        // 대댓글일 경우 부모 댓글 조회
        if (requestDto.getParentCommentId() != null) {
            parentComment = boardCommentRepository.findById(requestDto.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다. id=" + requestDto.getParentCommentId()));
            depth = parentComment.getDepth() + 1;
        }

        BoardComment comment = BoardComment.builder()
                .user(user)
                .post(post)
                .content(requestDto.getContent())
                .parentComment(parentComment)
                .depth(depth)
                .isDeleted(false)
                .build();

        boardCommentRepository.save(comment);
    }

    // 대댓글 조회
    @Transactional(readOnly = true)
    public CommentReplyListResponse getReplies(Long commentId, int page, int size, String sortStr) {
        // 조회 전, 부모 댓글 존재 여부 확인
        if (!boardCommentRepository.existsById(commentId)) {
            throw new IllegalArgumentException("해당 댓글이 존재하지 않습니다. commentId=" + commentId);
        }

        Pageable pageable = createPageable(page, size, sortStr);

        // 부모 댓글 ID를 기준으로 자식 댓글 페이징 조회
        Page<BoardComment> replyPage = boardCommentRepository.findByParentComment_CommentId(commentId, pageable);

        List<BoardPostResponse.CommentSimple> replyDtos = replyPage.getContent().stream()
                .map(BoardPostResponse.CommentSimple::from)
                .collect(Collectors.toList());

        PageInfo pageInfo = PageInfo.from(replyPage);

        return CommentReplyListResponse.builder()
                .replies(replyDtos)
                .pageInfo(pageInfo)
                .build();
    }

    // 게시글 수정
    @Transactional
    public void updatePost(Long postId, BoardPostRequest requestDto, String currentUserId) {
        // 1. 게시글 조회
        BoardPost post = boardPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. postId=" + postId));

        // 2. 작성자 검증 (게시글 작성자 ID vs 현재 로그인한 유저 ID)
        if (!post.getUser().getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException("게시글 수정 권한이 없습니다. 본인이 작성한 글만 수정 가능합니다.");
        }

        // 3. 게시글 업데이트 (JPA Dirty Checking)
        post.update(requestDto.getTitle(), requestDto.getContent(), requestDto.getCategory());
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId, String currentUserId) {
        // 1. 게시글 조회
        BoardPost post = boardPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. postId=" + postId));

        // 2. 작성자 검증
        if (!post.getUser().getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException("본인이 작성한 글만 삭제 가능합니다.");
        }

        // 3. 게시글 삭제 (Cascade 옵션으로 인해 댓글도 자동 삭제됨)
        boardPostRepository.delete(post);
    }

    // Pageable 생성 헬퍼 메서드
    private Pageable createPageable(int page, int size, String sortStr) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt"); // 기본값

        if (sortStr != null && !sortStr.isEmpty()) {
            String[] sortParams = sortStr.split(",");
            if (sortParams.length == 2) {
                String property = sortParams[0];
                String direction = sortParams[1];
                sort = direction.equalsIgnoreCase("asc") ?
                        Sort.by(Sort.Direction.ASC, property) :
                        Sort.by(Sort.Direction.DESC, property);
            }
        }
        return PageRequest.of(page, size, sort);
    }
}
