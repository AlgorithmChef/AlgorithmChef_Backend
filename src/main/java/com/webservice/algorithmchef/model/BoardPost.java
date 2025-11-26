package com.webservice.algorithmchef.model;

import com.webservice.algorithmchef.dto.board.BoardPostRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@Table(name = "board_post")
public class BoardPost {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_id")
	private Long postId;

	// User와의 다대일 관계 (작성자)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	private String category;

	@Column(nullable = false)
	private String title;

	// DB의 BLOB 타입에 대응. String으로 처리하되 대용량 데이터임을 명시
	@Lob
	@Column(nullable = false, columnDefinition = "LONGTEXT")
	private String content;

	// 게시글 삭제 시 댓글도 함께 삭제되도록 Cascade 설정
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<BoardComment> comments = new ArrayList<>();

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "modified_date")
	private LocalDateTime modifiedDate;

	// 게시글 수정 메서드 (Dirty Checking)
	public void update(String title, String content, String category) {
		this.title = title;
		this.content = content;
		this.category = category;
	}
}
