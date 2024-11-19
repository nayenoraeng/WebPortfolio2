package com.project.parkrental.Weather;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    // GET 요청을 통해 도시 이름을 받아 해당 도시의 날씨 데이터를 반환하는 엔드포인트
    @GetMapping("/weather/{city}")
    public WeatherData getWeather(@PathVariable String city) {
        return weatherService.getWeatherData(city);
    }
}