package com.project.parkrental.reservation;

import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class ReservationRequest {

    private String reserveDate;  // JSON에서 날짜를 문자열로 받음
    private LocalTime reserveTime;  // 시간은 Optional
    private Long costAll;
    private List<Long> productIdx;  // 배열로 받기 위해 List<Long>으로 수정
}
