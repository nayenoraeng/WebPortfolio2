package com.project.parkrental.parkList.util;

import com.project.parkrental.parkList.model.ParkList;
import com.project.parkrental.parkList.model.Product;
import com.project.parkrental.parkList.repository.ParkListRepository;
import com.project.parkrental.parkList.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class ProductDataGenerator implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ParkListRepository parkListRepository;

    private static final int THREAD_POOL_SIZE = 10; // 병렬로 사용할 스레드 수

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {  // 만약 제품이 하나도 없다면
            generateProductData();
        }
    }

    public void generateProductData() throws InterruptedException {
        List<ParkList> allParks = parkListRepository.findAll(); // 모든 공원 가져오기
        String[][] products = {
                {"p01", "Business A", "자전거", "8000"},
                {"p02", "Business B", "카메라", "5000"},
                {"p03", "Business B", "텐트", "10000"},
                {"p04", "Business B", "돗자리", "3000"},
                {"p05", "Business B", "테이블", "3000"},
                {"p06", "Business B", "파라솔", "5000"},
                {"p07", "Business B", "배낭", "3000"},
                {"p08", "Business A", "배드민턴세트", "5000"},
                {"p09", "Business C", "일회용품세트(판매)", "2000"},
                {"p10", "Business C", "우산(판매)", "8000"}
        };

        Random rand = new Random();

        // 스레드 풀 생성 (고정된 크기의 스레드 풀)
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        // 각 공원에 대한 작업을 병렬로 처리
        for (ParkList park : allParks) {
            executorService.submit(() -> {
                // 트랜잭션 처리된 메서드에서 데이터베이스 작업 수행
                saveProductsForPark(park, products, rand);
            });
        }

        // 모든 작업이 완료될 때까지 기다림
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.MINUTES);
    }

    @Transactional
    public void saveProductsForPark(ParkList park, String[][] products, Random rand) {
        // 공원의 카테고리에 따라 대여 불가능한 품목 목록을 가져옴
        List<String> unavailableProducts = getUnavailableProductsForCategory(park.getParkSe());

        for (String[] productData : products) {
            if (productRepository.findByParkListAndProductName(park, productData[2]).isEmpty()
                    && !unavailableProducts.contains(productData[2])) {
                int quantity;
                if (productData[2].equals("일회용품세트(판매)")) {
                    quantity = 100;
                } else if (productData[2].equals("우산(판매)")) {
                    quantity = 50;
                } else {
                    quantity = getQuantityBasedOnParkCategory(park.getParkSe(), rand);
                }

                Product product = new Product(null, productData[0], productData[1], productData[2],
                        quantity, Long.parseLong(productData[3]), park);
                productRepository.save(product);
            }
        }
    }

    // 공원 카테고리에 따라 대여 불가능한 품목을 설정
    public List<String> getUnavailableProductsForCategory(String parkCategory) {
        switch (parkCategory) {
            case "문화공원":
            case "역사공원":
            case "주제공원":
                return List.of("자전거", "배드민턴세트");
            default:
                return List.of();
        }
    }

    // 공원 카테고리별로 수량을 설정하는 메서드
    private int getQuantityBasedOnParkCategory(String parkCategory, Random rand) {
        switch (parkCategory) {
            case "국립공원":
                return rand.nextInt(30) + 20;
            case "소공원":
                return rand.nextInt(5) + 1;
            case "어린이공원":
                return rand.nextInt(10) + 5;
            case "근린공원":
                return rand.nextInt(20) + 5;
            case "수변공원":
                return rand.nextInt(30) + 10;
            case "체육공원":
                return rand.nextInt(30) + 10;
            case "역사공원":
                return rand.nextInt(20) + 5;
            case "문화공원":
                return rand.nextInt(20) + 5;
            default:
                return rand.nextInt(20) + 5;
        }
    }
}
