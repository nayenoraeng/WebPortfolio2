package com.project.parkrental.controller;

import com.project.parkrental.parkList.model.ParkList;
import com.project.parkrental.parkList.model.Product;
import com.project.parkrental.parkList.service.ParkListService;
import com.project.parkrental.parkList.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ParkListController {

    @Autowired
    private ParkListService parkListService;

    @Autowired
    private ProductService productService;

    // 비회원 공원 목록 페이지
    @GetMapping("/ParkList")
    public String showParkList(@RequestParam(value = "category", required = false) String category,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "latitude", required = false) Double latitude,
                               @RequestParam(value = "longitude", required = false) Double longitude,
                               @RequestParam(value = "radius", defaultValue = "10") Double radius,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "6") int size,
                               Model model) {

        Page<ParkList> parkPage;

        // 위치 기반 + 카테고리 검색 (키워드 없이)
        if (latitude != null && longitude != null && category != null && !category.isEmpty()) {
            parkPage = parkListService.getParksByLocationAndCategory(latitude, longitude, radius, category, PageRequest.of(page, size));
        }
        // 위치 기반 검색 (카테고리, 키워드 없이 전체 공원)
        else if (latitude != null && longitude != null && (category == null || category.isEmpty()) && (keyword == null || keyword.isEmpty())) {
            parkPage = parkListService.getParksByLocation(latitude, longitude, radius, PageRequest.of(page, size));
        }
        // 카테고리 + 키워드 검색 (위치 없이)
        else if (category != null && !category.isEmpty() && keyword != null && !keyword.isEmpty()) {
            parkPage = parkListService.getParksByCategoryAndKeyword(category, keyword, PageRequest.of(page, size));
        }
        // 카테고리 검색 (위치, 키워드 없이)
        else if (category != null && !category.isEmpty()) {
            parkPage = parkListService.getParksByParkSe(parkListService.getParkSeListByCategory(category), PageRequest.of(page, size));
        }
        // 전체 공원 + 키워드 검색 (카테고리 없이)
        else if ((category == null || category.isEmpty()) && keyword != null && !keyword.isEmpty()) {
            parkPage = parkListService.getParksByKeyword(keyword, PageRequest.of(page, size));
        }
        // 전체 공원 목록 (필터 없음)
        else {
            parkPage = parkListService.getAllParks(PageRequest.of(page, size));
        }

        // 모델에 데이터를 추가
        model.addAttribute("parkPage", parkPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("latitude", latitude);
        model.addAttribute("longitude", longitude);
        model.addAttribute("radius", radius);

        return "guest/ParkList";
    }

    // 공원 상세 페이지 (비회원도 접근 가능하게 변경)
    @GetMapping("/ParkDetail")
    public String showParkDetail(@RequestParam("parkId") Long parkId, Model model) {
        ParkList park = parkListService.getParkById(parkId);

        // parkId에 맞는 물품 리스트를 가져옴
        List<Product> products = productService.getProductsByParkId(parkId);

        model.addAttribute("park", park);
        model.addAttribute("products", products);

        return "guest/ParkDetail";
    }

    // 물품 상세 페이지
    @GetMapping("/Product/{id}")
    public String showProductDetail(@PathVariable("id") Long productId, Model model) {
        // productId에 해당하는 제품을 가져옴
        Product product = productService.getProductById(productId);

        // 모델에 제품을 추가
        model.addAttribute("product", product);

        return "guest/Product";
    }

    // 로그인 여부 확인 API
    @GetMapping("/guest/isLoggedIn")
    public ResponseEntity<Map<String, Boolean>> checkLoginStatus(Authentication authentication) {
        boolean loggedIn = authentication != null && authentication.isAuthenticated();
        Map<String, Boolean> response = new HashMap<>();
        response.put("loggedIn", loggedIn);
        return ResponseEntity.ok(response);
    }

    // 렌탈 가능한 제품 목록 페이지
    @GetMapping("/guest/Rental")
    public String showRentalPage(Model model, @RequestParam(value = "parkId", required = false, defaultValue = "1") Long parkId) {
        // parkId에 맞는 고유한 제품 목록을 가져옴
        List<Product> uniqueProducts = productService.getUniqueProductsByParkId(parkId);

        // 모델에 고유한 제품 목록을 추가
        model.addAttribute("products", uniqueProducts);

        return "guest/Rental";
    }
}
