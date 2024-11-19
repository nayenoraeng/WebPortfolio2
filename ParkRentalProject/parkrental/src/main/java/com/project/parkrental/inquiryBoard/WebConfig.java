package com.project.parkrental.inquiryBoard;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String filepath = "/Users/yerim/DevData/WebPortfolio2/ParkRentalProject/parkrental/src/main/resources/static/files/";
        System.out.println("Setting up resource handler with path: " + filepath);
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + filepath)
                .setCachePeriod(0);

    }

    private static final String DEVELOP_FRONT_ADDRESS = "http://localhost:8081";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(DEVELOP_FRONT_ADDRESS)
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
