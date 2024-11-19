package com.project.parkrental.Weather;

import java.util.List;

// 날씨 데이터를 저장하는 모델 클래스
public class WeatherData {
    private double currentTemperature;
    private String weatherCondition;
    private List<HourlyForecast> hourlyForecasts;

    public WeatherData(double currentTemperature, String weatherCondition, List<HourlyForecast> hourlyForecasts) {
        this.currentTemperature = currentTemperature;
        this.weatherCondition = weatherCondition;
        this.hourlyForecasts = hourlyForecasts;
    }

    public double getCurrentTemperature() {
        return currentTemperature;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public List<HourlyForecast> getHourlyForecasts() {
        return hourlyForecasts;
    }
}
