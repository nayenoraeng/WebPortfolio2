package com.project.parkrental.inquiryBoard;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReplyResponseDTO {

    private Long idx; // 답글 ID
    private String username; // 작성자
    private String content; // 내용
    private LocalDateTime postdate; // 작성일

    public ReplyResponseDTO(Long idx, String username, String content, String replyContent, LocalDateTime postdate) {
        this.idx = idx;
        this.username = username;
        this.content = content;
        this.postdate = postdate;
    }
}
