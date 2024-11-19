package com.project.parkrental.security.DTO;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserDto {
    private Long idx;
    private String name;
    private String username;
    private String email;
    private String phoneNum;
    private String postcode;
    private String address;
    private String detailAddress;
    private Timestamp regidate;
    private String authority;
    private int enabled;
    private String provider;
    private String providerId;
    private int isLocked;
    private int failCount;
    private Timestamp lockTimes;

    public UserDto() {}
    public UserDto(User user) {
        this.name = user.getName();
        this.username = user.getUsername();
        this.phoneNum = user.getPhoneNum();
        this.email = user.getEmail();
        this.postcode = user.getPostcode();
        this.address = user.getAddress();
        this.detailAddress = user.getDetailAddress();
        this.regidate = user.getRegidate();
        this.authority = user.getAuthority();
        this.enabled = user.getEnabled();
        this.provider = user.getProvider();
        this.providerId = user.getProviderId();
        this.isLocked = user.getIsLocked();
        this.failCount = user.getFailCount();
        this.lockTimes = user.getLockTimes();
    }

    public UserDto(Long idx, String username, String name,
                     String phoneNum, String email, String postcode, String address, String detailAddress,
                     Timestamp regidate, String authority, int enabled, String provider,
                     String providerId, int isLocked, int failCount, Timestamp lockTimes) {
        this.idx = idx;
        this.username = username;
        this.name = name;
        this.phoneNum = phoneNum;
        this.email = email;
        this.postcode = postcode;
        this.address = address;
        this.detailAddress = detailAddress;
        this.regidate = regidate;
        this.authority = authority;
        this.enabled = enabled;
        this.provider = provider;
        this.providerId = providerId;
        this.isLocked = isLocked;
        this.failCount = failCount;
        this.lockTimes = lockTimes;
    }
}
