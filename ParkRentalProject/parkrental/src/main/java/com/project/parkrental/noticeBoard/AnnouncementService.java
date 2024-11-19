package com.project.parkrental.noticeBoard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnnouncementService {


    @Autowired
    private AnnouncementRepository announcementRepository;


    // 공지사항 전체 조회
    public List<Announcement> getAllAnnouncements() {
        List<Announcement> announcements = announcementRepository.findAll();
        announcements.forEach(a -> System.out.println("Announcement: " + a.getTitle() + ", Postdate: " + a.getPostdate()));
        return announcements;
    }

    // ID로 공지사항 조회
    public Optional<Announcement> getAnnouncementById(Long id) {
        return announcementRepository.findById(id);
    }


    // 공지사항 생성
    public Announcement createAnnouncement(Announcement announcement) {
        return announcementRepository.save(announcement);
    }

    // 공지사항 수정
    public Announcement updateAnnouncement(Long idx, Announcement updatedAnnouncement) {
        Optional<Announcement> existingAnnouncement = announcementRepository.findById(idx);
        if (existingAnnouncement.isPresent()) {
            Announcement announcement = existingAnnouncement.get();
            announcement.setTitle(updatedAnnouncement.getTitle());
            announcement.setContent(updatedAnnouncement.getContent());
            announcement.setOfile(updatedAnnouncement.getOfile());
            announcement.setSfile(updatedAnnouncement.getSfile());
            System.out.println("Updating announcement with idx: " + idx); // 추가된 로그
            return announcementRepository.save(announcement); // 단일 save 호출
        } else {
            System.out.println("Announcement not found with idx: " + idx); // 추가된 로그
            return null; // 존재하지 않는 경우 null 반환
        }
    }
    // 페이징된 공지사항 목록을 가져오는 메서드
    public Page<Announcement> getAnnouncementsPaged(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);  // 페이지 번호와 페이지 크기를 설정
        if (search != null && !search.isEmpty()) {
            return searchAnnouncements(search, pageable); // 검색어가 있을 경우 검색 수행
        } else {
            return announcementRepository.findAll(pageable); // 검색어가 없으면 전체 목록 반환
        }
    }

    // 검색 기능: 제목 또는 내용에서 검색
    public Page<Announcement> searchAnnouncements(String keyword, Pageable pageable) {
        return announcementRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
    }

    // 제목으로 검색
    public Page<Announcement> searchAnnouncementsByTitle(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return announcementRepository.findByTitleContaining(title, pageable);
    }

    // 내용으로 검색
    public Page<Announcement> searchAnnouncementsByContent(String content, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return announcementRepository.findByContentContaining(content, pageable);
    }

    // 공지사항 삭제
    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }

    // 공지사항 저장
    public void save(Announcement announcement) {
        announcementRepository.save(announcement);
    }

    public Announcement findById(Long idx) {
        return null;
    }

    public void saveAnnouncement(Announcement announcement) {
    }


    // 검색 기능: 제목 또는 내용에서 검색
    public Page<Announcement> searchAnnouncements(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);  // Pageable 객체 생성

        // 제목 또는 내용에 검색어가 포함된 공지사항을 찾는 메서드 호출
        return announcementRepository.findByTitleContainingOrContentContaining(search, search, pageable);
    }
}
