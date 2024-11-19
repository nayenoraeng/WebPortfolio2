package com.project.parkrental.inquiryBoard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    Optional<Inquiry> findById(Long idx);

    @Modifying
    @Transactional
    @Query("UPDATE Inquiry i SET i.groupOrd = i.groupOrd + 1 WHERE i.originNo = :originNo AND i.groupOrd > :groupOrd")
    void updateGroupOrd(@Param("originNo") Long originNo, @Param("groupOrd") Integer groupOrd);

    @Query("SELECT i FROM Inquiry i ORDER BY i.originNo DESC, i.groupOrd ASC")
    Page<Inquiry> findAllOrderedByOriginAndGroupOrd(Pageable pageable);

    @Query("SELECT i FROM Inquiry i ORDER BY i.postdate DESC, i.originNo DESC, i.groupOrd ASC")
    Page<Inquiry> findAllOrderedByPostdate(Pageable pageable);

    // 제목으로 검색
    Page<Inquiry> findByTitleContaining(String title, Pageable pageable);

    // 내용으로 검색
    Page<Inquiry> findByContentContaining(String content, Pageable pageable);

    // 작성자로 검색
    Page<Inquiry> findByUsernameContaining(String username, Pageable pageable);
}
