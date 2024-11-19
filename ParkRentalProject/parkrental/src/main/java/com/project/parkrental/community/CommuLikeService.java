package com.project.parkrental.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CommuLikeService {

    @Autowired
    private CommuLikeRepository commuLikeRepository;

    @Autowired
    private CommunityRepository communityRepository;

    // 좋아요 기능 처리 메서드
    @Transactional
    public void toggleLike(String username, Long boardIdx) {
        // 게시글이 존재하는지 확인
        Community community = communityRepository.findById(boardIdx)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 사용자가 해당 게시글에 좋아요를 눌렀는지 확인
        Optional<CommuLike> existingLike = commuLikeRepository.findByUsernameAndCommunity(username, community);

        if (existingLike.isPresent()) {
            // 이미 좋아요를 눌렀다면 좋아요를 취소
            commuLikeRepository.delete(existingLike.get());
        } else {
            // 좋아요를 누르지 않았다면 좋아요 추가
            CommuLike like = new CommuLike();
            like.setUsername(username);
            like.setCommunity(community);
            like.setLikeCount(1);  // 좋아요 수는 1로 설정

            commuLikeRepository.save(like);
        }
    }

    // 게시글의 총 좋아요 수를 가져오는 메서드
    public long getLikeCount(Long boardIdx) {
        Community community = communityRepository.findById(boardIdx)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        return community.getLikes().size();
    }

}
