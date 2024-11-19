package com.project.parkrental.controller;

import com.project.parkrental.cart.Cart;
import com.project.parkrental.cart.CartRepository;
import com.project.parkrental.reservation.Reservation;
import com.project.parkrental.reservation.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/user/reservation")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private CartRepository cartRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createReservation(@RequestBody Map<String, Object> requestBody) {
        System.out.println("Request received with body: " + requestBody);

        // username 값 추출 및 검증
        String username = (String) requestBody.get("username");
        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest().body("Missing or invalid username");
        }

        // reserveDate 값 추출 및 검증
        String reserveDate = (String) requestBody.get("reserveDate");
        if (reserveDate == null || reserveDate.isEmpty()) {
            return ResponseEntity.badRequest().body("Missing or invalid reserveDate");
        }

        // costAll 값 추출 및 검증
        Long costAll;
        try {
            costAll = (requestBody.get("costAll") instanceof Integer)
                    ? Long.valueOf((Integer) requestBody.get("costAll"))
                    : (Long) requestBody.get("costAll");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid costAll value");
        }

        // cartIdx 값을 추출하고 Long 타입으로 변환
        Long cartIdx;
        try {
            cartIdx = (requestBody.get("cartIdx") instanceof Integer)
                    ? Long.valueOf((Integer) requestBody.get("cartIdx"))
                    : (Long) requestBody.get("cartIdx");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid cartIdx value");
        }

        Cart cart = cartRepository.findById(cartIdx)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // Product의 idx 값을 가져옴
        Long productIdx = cart.getProduct().getIdx();

        // reserveDate 문자열을 LocalDate로 변환
        LocalDate parsedReserveDate;
        try {
            parsedReserveDate = LocalDate.parse(reserveDate);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid date format for reserveDate");
        }

        System.out.println("Parsed username: " + username);
        System.out.println("Parsed reserveDate: " + reserveDate);
        System.out.println("Parsed costAll: " + costAll);
        System.out.println("Parsed cartIdx: " + cartIdx);

        // 예약 생성 로직 호출
        Reservation reservation = reservationService.createReservation(username, parsedReserveDate, costAll, productIdx);

        return ResponseEntity.ok(reservation);
    }

}
