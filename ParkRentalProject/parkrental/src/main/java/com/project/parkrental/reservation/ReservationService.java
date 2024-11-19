package com.project.parkrental.reservation;

import com.project.parkrental.parkList.model.Product;
import com.project.parkrental.parkList.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ProductRepository productRepository;

    public Reservation createReservation(String username, LocalDate reserveDate, Long costAll, Long productIdx) {
        // productIdx로 Product 객체 조회
        Product product = productRepository.findById(productIdx)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // Reservation 객체 생성 및 필드 설정
        Reservation reservation = new Reservation();
        reservation.setUsername(username);
        reservation.setReserveDate(reserveDate);  // 날짜만 처리
        reservation.setCostAll(costAll);
        reservation.setProductName(product.getProductName());
        reservation.setBusinessName(product.getBusinessName());
        reservation.setIsPaid(0);  // 결제 상태 기본값

        // 예약 번호 생성
        Reservation savedReservation = reservationRepository.save(reservation);
        String reserveNum = "R" + savedReservation.getIdx();
        savedReservation.setReserveNum(reserveNum);

        // 예약 번호 저장 후 최종적으로 다시 저장
        return reservationRepository.save(savedReservation);
    }
}
