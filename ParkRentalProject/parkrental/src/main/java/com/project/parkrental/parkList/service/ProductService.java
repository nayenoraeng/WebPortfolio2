package com.project.parkrental.parkList.service;

import com.project.parkrental.cart.Cart;
import com.project.parkrental.cart.CartRepository;
import com.project.parkrental.parkList.model.ParkList;
import com.project.parkrental.parkList.model.Product;
import com.project.parkrental.parkList.repository.ParkListRepository;
import com.project.parkrental.parkList.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ParkListRepository parkListRepository;

    @Autowired
    private CartRepository cartRepository;

    // 모든 제품 목록을 반환하는 메서드
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 특정 공원의 제품 목록을 반환하는 메서드
    public List<Product> getProductsByParkId(Long parkId) {
        // 해당 공원의 정보를 가져옴
        ParkList park = parkListRepository.findById(parkId)
                .orElseThrow(() -> new RuntimeException("공원을 찾을 수 없습니다."));

        // 공원의 카테고리에 맞는 대여 불가능한 품목 목록을 가져옴
        List<String> unavailableProducts = getUnavailableProductsForCategory(park.getParkSe());

        // 대여 불가능한 품목을 제외한 제품 목록을 반환
        return productRepository.findByParkList(park).stream()
                .filter(product -> !unavailableProducts.contains(product.getProductName()))
                .collect(Collectors.toList());
    }

    // 제품 idx로 제품 정보를 가져오는 메서드 (제품 상세 페이지용)
    public Product getProductById(Long idx) {
        Product product = productRepository.findById(idx)
                .orElseThrow(() -> new RuntimeException("해당 제품을 찾을 수 없습니다. idx: " + idx));

        // parkList와 parkId가 null인지 확인
        if (product.getParkList() == null) {
            throw new RuntimeException("해당 제품에 연결된 공원 정보가 없습니다.");
        }

        return product;
    }

    // 대여 불가능한 제품 리스트를 반환하는 메서드
    public List<String> getUnavailableProductsForCategory(String parkCategory) {
        switch (parkCategory) {
            case "문화공원":
            case "역사공원":
            case "주제공원":
                return List.of("자전거", "배드민턴세트");  // 운동용품은 대여 불가능
            default:
                return List.of();
        }
    }

    // 특정 물품 idx로 재고 차감
    public boolean reduceProductQuantity(Long idx, int quantity) {
        Product product = productRepository.findById(idx).orElse(null);
        if (product != null && product.getQuantity() >= quantity) {
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);
            return true;
        }
        return false;
    }
    // 특정 카테고리에서 대여 가능한 제품 목록을 반환하는 메서드
    public List<Product> getProductsByCategory(String parkCategory) {
        // 해당 카테고리에 맞는 대여 불가능한 제품 목록을 가져옴
        List<String> unavailableProducts = getUnavailableProductsForCategory(parkCategory);

        // 모든 제품 중에서 해당 카테고리에서 대여 가능한 제품만 필터링
        return productRepository.findAll().stream()
                .filter(product -> !unavailableProducts.contains(product.getProductName()))  // 대여 가능 여부 확인
                .collect(Collectors.toList());
    }

    // 특정 제품에 대한 여러 공원에서의 대여 가능 여부 확인
    public Map<ParkList, Boolean> getAvailabilityForProductInParks(String productName) {
        List<ParkList> parks = parkListRepository.findAll();  // 모든 공원 리스트를 가져옴
        Map<ParkList, Boolean> availabilityMap = new HashMap<>();

        // 각 공원에 대해 해당 제품의 대여 가능 여부를 확인
        for (ParkList park : parks) {
            List<String> unavailableProducts = getUnavailableProductsForCategory(park.getParkSe());

            // 제품이 대여 불가능한 목록에 있는지 확인
            boolean isAvailable = !unavailableProducts.contains(productName);
            availabilityMap.put(park, isAvailable);  // 공원과 대여 가능 여부를 매핑
        }

        return availabilityMap;
    }

    // 고유한 제품 목록을 반환 (productNum 기준)
    public List<Product> getUniqueProductsByParkId(Long parkId) {
        return productRepository.findUniqueProductsByPark(parkId);
    }

    // 특정 사용자의 장바구니에 담긴 제품 정보를 반환하는 메서드
    public List<Cart> getCartProductsByUsername(String username) {
        // 사용자의 장바구니 목록을 가져옴
        List<Cart> carts = cartRepository.findByUsername(username);

        // 장바구니가 없으면 빈 목록을 반환
        if (carts == null || carts.isEmpty()) {
            return List.of();
        }

        // 각 Cart 객체에서 제품 번호와 수량을 가져옴
        return carts; // Cart 자체를 반환하여 각 장바구니 항목에 접근 가능
    }

    // 장바구니의 항목에 맞는 제품을 조회하는 메서드
    public void updateQuantity(Long cartIdx, int newQuantity) {
        Cart cart = cartRepository.findById(cartIdx)
                .orElseThrow(() -> new RuntimeException("장바구니 항목을 찾을 수 없습니다."));

        // productName과 parkId로 Product를 조회
        Product product = productRepository.findByProductNameAndParkId(cart.getProductName(), cart.getParkId())
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다. 상품명: "
                        + cart.getProductName() + ", 공원 ID: " + cart.getParkId()));

        int availableQuantity = product.getQuantity();

        if (newQuantity > availableQuantity) {
            throw new RuntimeException("재고가 부족합니다. 최대 " + availableQuantity + "개까지 설정할 수 있습니다.");
        }

        cart.setQuantity(newQuantity);
        cartRepository.save(cart);
    }

    // 추가된 메서드: productName과 parkId로 Product를 조회
    public Product getProductByNameAndParkId(String productName, Long parkId) {
        // parkId로 공원을 조회
        ParkList park = parkListRepository.findById(parkId)
                .orElseThrow(() -> new RuntimeException("공원을 찾을 수 없습니다."));

        // productName과 parkId로 Product를 조회하여 반환
        return productRepository.findByProductNameAndParkId(productName, parkId)
                .orElseThrow(() -> new RuntimeException("해당 공원의 상품을 찾을 수 없습니다: " + productName));
    }

    public Product findByName(String productName) {
        return productRepository.findByProductName(productName);
    }
}
