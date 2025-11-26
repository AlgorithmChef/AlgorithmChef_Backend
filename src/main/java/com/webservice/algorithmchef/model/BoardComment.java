package com.webservice.algorithmchef.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "board_comment")
public class BoardComment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")
	private Long commentId;

	// 댓글이 달린 게시글
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false)
	private BoardPost post;

	// 댓글 작성자
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// 대댓글 기능을 위한 자기 참조 (부모 댓글)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_comment_id")
	private BoardComment parentComment;

	// 대댓글 목록 (자식 댓글)
	@OneToMany(mappedBy = "parentComment", orphanRemoval = true)
	@Builder.Default
	private List<BoardComment> children = new ArrayList<>();

	@Column(nullable = false)
	@ColumnDefault("0")
	private int depth;

	@Lob
	@Column(nullable = false, columnDefinition = "LONGTEXT")
	private String content;

	// 삭제 여부
	@Column(nullable = false)
	@ColumnDefault("false")
	private boolean isDeleted;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "modified_date")
	private LocalDateTime modifiedDate;

	// 댓글 내용 수정
	public void updateContent(String content) {
		this.content = content;
	}

	// 댓글 삭제 처리
	public void changeIsDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
}