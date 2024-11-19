package com.project.parkrental.noticeBoard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    Page<Announcement> findAll(Pageable pageable); // 전체 공지사항 조회
    // 제목 또는 내용에서 검색하는 메서드
    Page<Announcement> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    Page<Announcement> findByTitleContaining(String title, Pageable pageable);

    Page<Announcement> findByContentContaining(String content, Pageable pageable);

    // 필요한 추가 쿼리 메서드를 정의할 수 있음
}
