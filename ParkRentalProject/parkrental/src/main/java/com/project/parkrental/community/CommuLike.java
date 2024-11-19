package com.project.parkrental.community;

import jakarta.persistence.*;

@Entity
@Table(name = "commuLike")
@IdClass(CommuLikeId.class)  // 복합 키 사용을 위해 IdClass 지정
public class CommuLike {

    @Id
    @Column(name = "username", nullable = false)
    private String username;  // 좋아요를 누른 사용자 ID

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boardIdx", referencedColumnName = "idx")
    private Community community;  // 좋아요가 눌린 게시글

    @Column(name = "likeCount", columnDefinition = "int(10) DEFAULT 1")
    private int likeCount;  // 좋아요 수, 기본값은 1로 설정

    // 기본 생성자
    public CommuLike() {}

    // Getter와 Setter 메서드
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
