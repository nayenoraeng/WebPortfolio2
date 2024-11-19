package com.project.parkrental.community;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long> {
    Page<Community> findAll(Pageable pageable); // 전체 Community 게시물 조회

    // 제목 또는 내용에서 검색하는 메서드
    Page<Community> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    Page<Community> findByTitleContaining(String title, Pageable pageable);

    Page<Community> findByContentContaining(String content, Pageable pageable);

    // 필요한 추가 쿼리 메서드를 정의할 수 있음
}
