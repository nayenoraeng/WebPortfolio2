package com.project.parkrental.security.DTO;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "seller")
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idx;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "^\\d{3}-\\d{2}-\\d{5}$")
    @NotNull(message = "사업자번호는 필수 입력값입니다.")
    private String businessNum;

    @Column(nullable = false, unique = true)
    @NotNull(message="아이디는 필수입력값입니다.")
    private String username;

    @Column(nullable = false)
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[~!@#$%^&*+=()_-])(?=.*[0-9]).+$",
            message = "비밀번호는 8~16자 영문 대소문자, 숫자, 특수문자를 사용하세요.")
    @NotNull(message="비밀번호는 필수 입력값입니다.")
    private String password;

    @Transient
    private String confirmPassword;

    @Column(nullable = false)
    @NotNull(message="이름은 필수 입력 값입니다.")
    private String name;

    @Column(nullable = false)
    @NotNull(message="상호는 필수 입력 값입니다.")
    private String businessName;

    @Column(nullable = false)
    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
    @NotNull(message = "휴대전화번호는 필수 입력 값입니다.")
    private String phoneNum;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "^[A-Za-z0-9%+-]+@[A-za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식이 올바르지 않습니다.")
    @NotNull(message="이메일은 필수 입력 값입니다.")
    private String email;

    private String postcode;
    private String address;
    private String detailAddress;

    @Builder.Default
    private Timestamp regidate = new Timestamp(System.currentTimeMillis());
    private String authority;

    @Builder.Default
    private int enabled = 1;

    @Builder.Default
    private String provider = "LOCAL";
    private String providerId;
    private int isLocked;
    private int failCount;
    private Timestamp lockTimes;

}
