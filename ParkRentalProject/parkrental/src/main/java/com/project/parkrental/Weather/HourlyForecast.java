package com.project.parkrental.Weather;

// 시간별 예보 데이터를 나타내는 모델 클래스
public class HourlyForecast {
    private String time;
    private double temperature;
    private String weatherIcon;

    public HourlyForecast(String time, double temperature, String weatherIcon) {
        this.time = time;
        this.temperature = temperature;
        this.weatherIcon = weatherIcon;
    }

    public String getTime() {
        return time;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }
}