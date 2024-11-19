package com.project.parkrental.community;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CommuLikeRepository extends JpaRepository<CommuLike, CommuLikeId> {
    // 특정 게시글에 대해 사용자가 좋아요를 눌렀는지 확인하는 메서드
    Optional<CommuLike> findByUsernameAndCommunity(String username, Community community);
}
