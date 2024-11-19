package com.project.parkrental.community;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommunityService {

    @Autowired
    private CommunityRepository communityRepository;

    // 커뮤니티 전체 조회
    public List<Community> getAllCommunities() {
        List<Community> communities = communityRepository.findAll();
        communities.forEach(c -> System.out.println("Community: " + c.getTitle() + ", Postdate: " + c.getPostdate()));
        return communities;
    }

    // ID로 커뮤니티 조회
    public Optional<Community> getCommunityById(Long id) {
        return communityRepository.findById(id);
    }

    // 커뮤니티 생성
    public Community createCommunity(Community community) {
        return communityRepository.save(community);
    }

    // 커뮤니티 수정
    public Community updateCommunity(Long idx, Community updatedCommunity) {
        Optional<Community> existingCommunity = communityRepository.findById(idx);
        if (existingCommunity.isPresent()) {
            Community community = existingCommunity.get();
            community.setTitle(updatedCommunity.getTitle());
            community.setContent(updatedCommunity.getContent());
            community.setOfile(updatedCommunity.getOfile());
            community.setSfile(updatedCommunity.getSfile());
            System.out.println("Updating community with idx: " + idx); // 추가된 로그
            return communityRepository.save(community); // 단일 save 호출
        } else {
            System.out.println("Community not found with idx: " + idx); // 추가된 로그
            return null; // 존재하지 않는 경우 null 반환
        }
    }

    // 페이징된 커뮤니티 목록을 가져오는 메서드
    public Page<Community> getCommunitiesPaged(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);  // 페이지 번호와 페이지 크기를 설정
        if (search != null && !search.isEmpty()) {
            return searchCommunities(search, pageable); // 검색어가 있을 경우 검색 수행
        } else {
            return communityRepository.findAll(pageable); // 검색어가 없으면 전체 목록 반환
        }
    }

    // 검색 기능: 제목 또는 내용에서 검색
    public Page<Community> searchCommunities(String keyword, Pageable pageable) {
        return communityRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
    }

    // 제목으로 검색
    public Page<Community> searchCommunitiesByTitle(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return communityRepository.findByTitleContaining(title, pageable);
    }

    // 내용으로 검색
    public Page<Community> searchCommunitiesByContent(String content, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return communityRepository.findByContentContaining(content, pageable);
    }

    // 커뮤니티 삭제
    public void deleteCommunity(Long id) {
        communityRepository.deleteById(id);
    }

    // 커뮤니티 저장
    public void save(Community community) {
        communityRepository.save(community);
    }

    public Community findById(Long idx) {
        return null;
    }

    public void saveCommunity(Community community) {
    }

    // 검색 기능: 제목 또는 내용에서 검색
    public Page<Community> searchCommunities(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);  // Pageable 객체 생성

        // 제목 또는 내용에 검색어가 포함된 커뮤니티를 찾는 메서드 호출
        return communityRepository.findByTitleContainingOrContentContaining(search, search, pageable);
    }
}