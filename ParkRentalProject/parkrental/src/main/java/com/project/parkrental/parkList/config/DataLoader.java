package com.project.parkrental.parkList.config;

import com.project.parkrental.parkList.service.ParkListService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final ParkListService parkListService;

    public DataLoader(ParkListService parkListService) {
        this.parkListService = parkListService;
    }

    // 어플리케이션 시작 시 실행되는 메서드
    @Override
    public void run(String... args) throws Exception {
        // 데이터베이스에 저장된 데이터가 없는 경우, API 데이터를 불러와 저장
        if (parkListService.isDatabaseEmpty()) {
            parkListService.saveParksToDatabase(); // 서버 시작 시 API 데이터를 불러와 DB에 저장
        } else {
            // 데이터가 이미 존재하면 API 호출을 생략
            System.out.println("업데이트할 데이터가 없습니다. API 호출을 생략합니다.");
        }
    }
}
