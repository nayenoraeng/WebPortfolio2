package com.project.parkrental.inquiryBoard;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryRequestDTO {

    private Long idx;
    private Long originNo;
    private Integer groupOrd;
    private Integer groupLayer;
    private String username;
    @NotBlank(message = "제목을 입력해주세요.", groups = {WriteGroup.class})
    @Size(max = 50, message = "제목은 50자 이내로 입력해주세요.", groups = {WriteGroup.class})
    private String title;
    @NotBlank(message = "내용을 입력해주세요.", groups = {WriteGroup.class})
    @Size(max = 500, message = "내용은 500자 이내로 입력해주세요.", groups = {WriteGroup.class})
    private String content;
    private LocalDateTime postdate;
    private Integer viewCount;
    private Integer responses;
    private String ofile;
    private String sfile;
    @NotBlank(message = "비밀번호를 입력해주세요.", groups = {WriteGroup.class})
    @Size(min = 4, max = 4, message = "비밀번호는 정확히 4자 입력해주세요.", groups = {WriteGroup.class})
    @Pattern(regexp = "^[0-9]+$", message = "비밀번호는 숫자만 포함되어야 합니다.", groups = {WriteGroup.class})
    private String inquiryPassword;


}
