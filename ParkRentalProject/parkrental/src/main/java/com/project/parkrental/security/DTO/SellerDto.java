package com.project.parkrental.security.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class SellerDto {
    private long idx;
    private String name;
    private String username;
    private String businessName;
    private String businessNum;
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
    private Timestamp lockTimes;
    private int failCount;

    public SellerDto(Seller seller) {
        this.idx = seller.getIdx();
        this.name = seller.getName();
        this.username = seller.getUsername();
        this.businessNum = seller.getBusinessNum();
        this.email = seller.getEmail();
        this.phoneNum = seller.getPhoneNum();
        this.postcode = seller.getPostcode();
        this.address = seller.getAddress();
        this.detailAddress = seller.getDetailAddress();
        this.regidate = seller.getRegidate();
        this.authority = seller.getAuthority();
        this.enabled = seller.getEnabled();
        this.provider = seller.getProvider();
        this.providerId = seller.getProviderId();
        this.isLocked = seller.getIsLocked();
        this.failCount = seller.getFailCount();
        this.lockTimes = seller.getLockTimes();
    }

    public SellerDto(Long idx, String businessNum, String username, String name, String businessName,
                     String phoneNum, String email, String postcode, String address, String detailAddress,
                     Timestamp regidate, String authority, int enabled, String provider,
                     String providerId, int isLocked, int failCount, Timestamp lockTimes) {
        this.idx = idx;
        this.businessNum = businessNum;
        this.username = username;
        this.name = name;
        this.businessName = businessName;
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
