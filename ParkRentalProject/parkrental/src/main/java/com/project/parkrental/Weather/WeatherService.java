package com.project.parkrental.Weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

// 날씨 데이터를 OpenWeatherMap API에서 가져오는 서비스 클래스
@Service
public class WeatherService {

    // OpenWeatherMap API 정보
    private static final String API_KEY = "f69b4e9aa8d5fb506f44f05664594967";  // 발급받은 API 키
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/forecast?q={city}&appid={apiKey}&units=metric";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WeatherService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    // 도시 이름을 받아 해당 도시의 시간대별 날씨 정보를 반환하는 메서드
    public WeatherData getWeatherData(String city) {
        String url = API_URL.replace("{city}", city).replace("{apiKey}", API_KEY);
        String jsonResponse = restTemplate.getForObject(url, String.class);

        try {
            // JSON 응답 파싱
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            // 현재 온도 및 상태 (최신 데이터 사용)
            JsonNode currentWeatherNode = rootNode.path("list").get(0);
            double currentTemp = currentWeatherNode.path("main").path("temp").asDouble();
            String weatherCondition = currentWeatherNode.path("weather").get(0).path("main").asText();

            // 시간대별 예보 정보 추출
            List<HourlyForecast> forecasts = new ArrayList<>();
            for (int i = 0; i < rootNode.path("list").size(); i++) {
                JsonNode forecastNode = rootNode.path("list").get(i);
                String time = forecastNode.path("dt_txt").asText();  // 시간대 추출
                double temp = forecastNode.path("main").path("temp").asDouble();
                String weatherIcon = forecastNode.path("weather").get(0).path("icon").asText();

                // HourlyForecast 객체 생성 후 리스트에 추가
                forecasts.add(new HourlyForecast(time, temp, weatherIcon));
            }

            // WeatherData 객체 생성 후 반환
            return new WeatherData(currentTemp, weatherCondition, forecasts);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
