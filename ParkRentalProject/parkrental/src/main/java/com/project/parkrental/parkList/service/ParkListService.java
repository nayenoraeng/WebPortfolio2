package com.project.parkrental.parkList.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.project.parkrental.parkList.model.ParkList;
import com.project.parkrental.parkList.repository.ParkListRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ParkListService {

    private final ParkListRepository parkListRepository;

    public ParkListService(ParkListRepository parkListRepository) {
        this.parkListRepository = parkListRepository;
    }

    // API URL과 Key -> 나중에 본인 키로 수정하기 :)
    private static final String API_URL = "http://api.data.go.kr/openapi/tn_pubr_public_cty_park_info_api";
    private static final String API_KEY = "5yi/JDgGTeVp6+xg83HX82guKjxNXk7tArUgUa0l0T/ZkM3fSr8kFqBwr1sdAwyYsQacld/jhJhuTCSvbrdzLQ==";

    // 데이터베이스에 저장된 공원 데이터가 있는지 확인
    public boolean isDatabaseEmpty() {
        long count = parkListRepository.count(); // 데이터베이스에 저장된 공원 수
        System.out.println("현재 데이터베이스에 있는 데이터 수: " + count);
        return count == 0;  // 데이터가 하나도 없으면 true 반환
    }

    // 공원 데이터를 API에서 가져와 데이터베이스에 저장
    public void saveParksToDatabase() {
        // 데이터베이스에 저장된 공원 목록을 확인
        List<ParkList> existingParks = parkListRepository.findAll();

        // API로부터 공원 목록 데이터를 불러옴
        List<ParkList> parksFromApi = fetchParksFromApi();

        // 데이터베이스에 저장된 데이터가 없으면 전부 저장
        if (existingParks.isEmpty()) {
            parkListRepository.saveAll(parksFromApi);
        } else {
            // 중복되지 않은 데이터만 필터링
            for (ParkList parkFromApi : parksFromApi) {
                boolean isDuplicate = existingParks.stream().anyMatch(existingPark ->
                        existingPark.getParkNm().equals(parkFromApi.getParkNm()) &&
                                existingPark.getLatitude().equals(parkFromApi.getLatitude()) &&
                                existingPark.getLongitude().equals(parkFromApi.getLongitude()));
                
                // 중복되지 않은 데이터만 저장
                if (!isDuplicate) {
                    parkListRepository.save(parkFromApi);
                }
            }
        }
    }

    // API 데이터를 가져옴
    public List<ParkList> fetchParksFromApi() {
        RestTemplate restTemplate = new RestTemplate();
        List<ParkList> parks = new ArrayList<>();
        int pageNo = 1;
        int numOfRows = 100;

        try {
            String encodedServiceKey = URLEncoder.encode(API_KEY, StandardCharsets.UTF_8.toString());
            while (true) {
                // API URL 생성
                String url = API_URL + "?serviceKey=" + encodedServiceKey
                        + "&numOfRows=" + numOfRows
                        + "&pageNo=" + pageNo
                        + "&type=json";

                // URL을 URI 객체로 변환
                URI uri = new URI(url);
                String response = restTemplate.getForObject(uri, String.class);

                // JSON 데이터를 매핑하기 위한 객체
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

                // 응답 JSON을 파싱
                JsonNode rootNode = objectMapper.readTree(response);
                // 공원 목록 추출
                JsonNode items = rootNode.path("response").path("body").path("items");

                if (items.isEmpty()) {
                    break;  // 더 이상 불러올 데이터가 없으면 종료
                }

                // 각 공원 데이터를 ParkList 객체로 변환하여 리스트에 추가
                for (JsonNode item : items) {
                    String parkNm = item.path("parkNm").asText();
                    String parkSe = item.path("parkSe").asText();
                    String lnmadr = item.path("lnmadr").asText();
                    String parkAr = item.path("parkAr").asText();
                    Double latitude = item.path("latitude").asDouble();
                    Double longitude = item.path("longitude").asDouble();

                    if (latitude == null || longitude == null) {
                        continue; // 위도나 경도가 없으면 건너뜀
                    }

                    ParkList park = new ParkList(parkNm, parkSe, lnmadr, parkAr, latitude, longitude);
                    parks.add(park);
                }
                pageNo++; // 다음 페이지로 넘어감
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return parks; // API에서 받은 공원 데이터를 반환
    }

    // 개별 공원 데이터를 데이터베이스에 저장
    public ParkList savePark(ParkList parkList) {
        return parkListRepository.save(parkList);
    }

    // 전체 공원 목록을 페이징으로 가져옴
    public Page<ParkList> getAllParks(Pageable pageable) {
        return parkListRepository.findAll(pageable);
    }

    // 키워드를 통한 검색 (공원명 또는 주소 기반)
    public Page<ParkList> getParksByKeyword(String keyword, PageRequest pageRequest) {
        return parkListRepository.findByParkNmContainingOrLnmadrContaining(keyword, keyword, pageRequest);
    }

    // 카테고리에 맞는 공원 구분 리스트를 가져옴
    public List<String> getParkSeListByCategory(String category) {
        if ("natural".equals(category)) {
            return List.of("국립공원", "자연공원", "수변공원", "체육공원");
        } else if ("urban".equals(category)) {
            return List.of("근린공원", "소공원", "어린이공원");
        } else if ("special".equals(category)) {
            return List.of("주제공원", "묘지공원", "문화공원", "기타공원", "역사공원");
        } else {
            return List.of();
        }
    }

    // 카테고리와 키워드를 기반으로 공원 목록을 가져옴
    public Page<ParkList> getParksByCategoryAndKeyword(String category, String keyword, Pageable pageable) {
        List<String> parkSeList = getParkSeListByCategory(category);

        if (parkSeList.isEmpty()) {
            return parkListRepository.findByParkNmContainingOrLnmadrContaining(keyword, keyword, pageable);
        } else {
            return parkListRepository.findByCategoryAndKeyword(parkSeList, keyword, pageable);
        }
    }

    // 카테고리별로 공원 목록을 페이징 처리하여 가져옴
    public Page<ParkList> getParksByParkSe(List<String> parkSeList, Pageable pageable) {
        if (!parkSeList.isEmpty()) {
            return parkListRepository.findByParkSeIn(parkSeList, pageable);
        } else {
            return Page.empty(pageable);
        }
    }

    // 위치 기반 공원 검색 및 페이징 처리
    public Page<ParkList> getParksByLocation(double latitude, double longitude, double radiusKm, Pageable pageable) {
        List<ParkList> allParks = parkListRepository.findAll(); // 모든 공원 가져옴

        // 각 공원의 거리 계산 후 필터링
        List<ParkList> parksByLocation = allParks.stream()
                .map(park -> {
                    double distance = calculateDistance(latitude, longitude, park.getLatitude(), park.getLongitude());
                    park.setDistanceFromUser(distance); // 거리 설정
                    return park;
                })
                .filter(park -> park.getDistanceFromUser() <= radiusKm) // 반경 내 공원만 필터링
                .sorted(Comparator.comparing(ParkList::getDistanceFromUser)) // 가까운 순서로 정렬
                .collect(Collectors.toList());

        // 페이징 처리
        int start = Math.min((int) pageable.getOffset(), parksByLocation.size());
        int end = Math.min((start + pageable.getPageSize()), parksByLocation.size());
        return new PageImpl<>(parksByLocation.subList(start, end), pageable, parksByLocation.size());
    }

    // 카테고리와 위치를 기반으로 공원 목록 가져옴
    public Page<ParkList> getParksByLocationAndCategory(Double latitude, Double longitude, Double radius, String category, Pageable pageable) {
        List<String> parkSeList = getParkSeListByCategory(category);
        List<ParkList> parksByLocation = parkListRepository.findByLocationAndParkSeIn(latitude, longitude, radius, parkSeList);

        // 거리 계산 추가 및 가까운 순서 정렬
        parksByLocation.forEach(park -> {
            double distance = calculateDistance(latitude, longitude, park.getLatitude(), park.getLongitude());
            park.setDistanceFromUser(distance);
        });

        parksByLocation = parksByLocation.stream()
                .sorted(Comparator.comparing(ParkList::getDistanceFromUser))
                .collect(Collectors.toList());

        // 페이징 처리 자동으로 적용
        int start = Math.min((int) pageable.getOffset(), parksByLocation.size());
        int end = Math.min((start + pageable.getPageSize()), parksByLocation.size());
        return new PageImpl<>(parksByLocation.subList(start, end), pageable, parksByLocation.size());
    }

    // 카테고리, 키워드, 위치 기반 필터링 후 결과를 페이징 처리
    public Page<ParkList> getParksByCategoryKeywordAndLocation(String category, String keyword, Double latitude, Double longitude, Double radius, Pageable pageable) {
        // 카테고리 필터링을 위한 공원 구분 리스트
        List<String> parkSeList = getParkSeListByCategory(category);
        // 위치 기반으로 필터링된 공원 리스트
        List<ParkList> parksByLocation = parkListRepository.findByLocationAndParkSeIn(latitude, longitude, radius, parkSeList);

        // 위치 기반으로 필터된 공원 중 키워드에 맞는 공원 검색
        List<ParkList> filteredParks = parksByLocation.stream()
                .filter(park -> (keyword == null || keyword.isEmpty() || park.getParkNm().contains(keyword) || park.getLnmadr().contains(keyword))) // 키워드 필터링
                .sorted(Comparator.comparing(ParkList::getDistanceFromUser)) // 거리 순으로 정렬
                .collect(Collectors.toList());

        // 페이지네이션 처리
        int start = Math.min((int) pageable.getOffset(), filteredParks.size());
        int end = Math.min((start + pageable.getPageSize()), filteredParks.size());

        return new PageImpl<>(filteredParks.subList(start, end), pageable, filteredParks.size());
    }

    // 위치와 키워드 기반 공원 검색
    public Page<ParkList> getParksByKeywordAndLocation(String keyword, double latitude, double longitude, double radiusKm, Pageable pageable) {
        List<ParkList> allParks = parkListRepository.findAll(); // 모든 공원 가져옴

        // 위치와 키워드 기반으로 필터링된 공원 리스트 생성
        List<ParkList> parksByLocationAndKeyword = allParks.stream()
                .filter(park -> park.getParkNm().contains(keyword) || park.getLnmadr().contains(keyword)) // 키워드 필터
                .map(park -> {
                    double distance = calculateDistance(latitude, longitude, park.getLatitude(), park.getLongitude());
                    park.setDistanceFromUser(distance); // 거리 설정
                    return park;
                })
                .filter(park -> park.getDistanceFromUser() <= radiusKm) // 반경 내 공원만 필터링
                .sorted(Comparator.comparing(ParkList::getDistanceFromUser)) // 가까운 순서로 정렬
                .collect(Collectors.toList());

        // 페이징 처리
        int start = Math.min((int) pageable.getOffset(), parksByLocationAndKeyword.size());
        int end = Math.min((start + pageable.getPageSize()), parksByLocationAndKeyword.size());
        return new PageImpl<>(parksByLocationAndKeyword.subList(start, end), pageable, parksByLocationAndKeyword.size());
    }

    // 두 지점 간 거리 계산 (Haversine 공식 사용)
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;  // 지구 반경 (km)
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // km로 반환
    }

    public ParkList getParkById(Long parkId) {
        return parkListRepository.findById(parkId)
                .orElseThrow(() -> new RuntimeException("공원을 찾을 수 없습니다."));
    }
}
