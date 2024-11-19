package com.project.parkrental.parkList.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Table(name = "parkList")
@AllArgsConstructor
@NoArgsConstructor
public class ParkList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 자동 증가되는 PK 설정
    @Column(name = "parkId")  // 테이블의 parkId 컬럼과 매핑
    private Long parkId;     // 자동 증가되는 고유 ID

    @JsonProperty("PARK_NM")   // JSON 데이터에서 'PARK_NM' 필드를 매핑
    @Column(name = "parkNm")   // 테이블의 parkNm 컬럼과 매핑
    private String parkNm;     // 공원 이름

    @JsonProperty("PARK_SE")   // JSON 데이터에서 'PARK_SE' 필드를 매핑
    @Column(name = "parkSe")   // 테이블의 parkSe 컬럼과 매핑
    private String parkSe;     // 공원 구분

    @JsonProperty("LNMADR")    // JSON 데이터에서 'LNMADR' 필드를 매핑
    @Column(name = "lnmadr")   // 테이블의 lnmadr 컬럼과 매핑
    private String lnmadr;     // 소재지 주소

    @JsonProperty("PARK_AR")   // JSON 데이터에서 'PARK_AR' 필드를 매핑
    @Column(name = "parkAr")   // 테이블의 parkAr 컬럼과 매핑
    private String parkAr;     // 공원 면적

    @JsonProperty("LATITUDE")  // JSON 데이터에서 'LATITUDE' 필드를 매핑
    @Column(name = "latitude") // 테이블의 latitude 컬럼과 매핑
    private Double latitude;   // 공원의 위도

    @JsonProperty("LONGITUDE")  // JSON 데이터에서 'LONGITUDE' 필드를 매핑
    @Column(name = "longitude") // 테이블의 longitude 컬럼과 매핑
    private Double longitude;   // 공원의 경도

    // 사용자와의 거리 저장 (DB에 저장하지 않으므로 @Transient 적용)
    @Transient
    private double distanceFromUser;

    // Product와의 양방향 연관관계 설정 (Product는 ParkList를 참조)
    @OneToMany(mappedBy = "parkList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;

    public ParkList(String parkNm, String parkSe, String lnmadr, String parkAr, Double latitude, Double longitude) {
        this.parkNm = parkNm;
        this.parkSe = parkSe;
        this.lnmadr = lnmadr;
        this.parkAr = parkAr;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
