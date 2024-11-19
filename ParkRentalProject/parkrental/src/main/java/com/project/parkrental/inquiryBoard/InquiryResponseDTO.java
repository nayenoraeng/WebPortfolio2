package com.project.parkrental.inquiryBoard;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ToString
@Getter @Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InquiryResponseDTO {

    private Long idx;
    private Long originNo;
    private Integer groupOrd;
    private Integer groupLayer;
    private String username;
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
    private LocalDateTime postdate;
    private Integer viewCount;
    private Integer responses;
    private String ofile;
    private String sfile;

    public InquiryResponseDTO(Long idx, Integer groupOrd, Integer groupLayer, String title, String username, String content, LocalDateTime postdate,
                              Integer viewCount, Integer responses)
    {
        this.idx = idx;
        this.groupOrd = groupOrd;
        this.groupLayer = groupLayer;
        this.title = title;
        this.username = username;
        this.content = content;
        this.postdate = postdate;
        this.viewCount = viewCount;
        this.responses = responses;
    }

}
