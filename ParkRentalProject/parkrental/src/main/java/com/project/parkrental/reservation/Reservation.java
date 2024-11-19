package com.project.parkrental.reservation;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false, unique = true)
    private String reserveNum;

    @Column(nullable = false)
    private LocalDate reserveDate;

    @Column(nullable = false)
    private LocalTime reserveTime;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private int isPaid; // 결제 상태 (1 = 결제 완료, 0 = 결제 안됨)

    @Column(nullable = false)
    private Long costAll;

    // 판매자 이름 (businessName 참조)
    @Column(nullable = false)
    private String businessName;

    // 상품 이름 (productName 참조)
    @Column(nullable = false)
    private String productName;
}
