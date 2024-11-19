package com.project.parkrental.cart;

import com.project.parkrental.parkList.model.Product;
import com.project.parkrental.parkList.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    public void addProductToCart(String username, Product product, int quantity) {
        // 장바구니에서 이미 해당 상품이 있는지 확인
        Cart existingCart = cartRepository.findByUsernameAndProduct(username, product);

        int availableQuantity = product.getQuantity();

        // 장바구니에 이미 해당 상품이 있는 경우 수량을 추가
        if (existingCart != null) {
            int totalQuantity = existingCart.getQuantity() + quantity;
            if (totalQuantity > availableQuantity) {
                throw new RuntimeException("재고가 부족합니다. 최대 " + availableQuantity + "개까지 추가할 수 있습니다.");
            }
            existingCart.setQuantity(totalQuantity);
            cartRepository.save(existingCart);
        } else {
            // 장바구니에 없으면 새로운 항목을 추가
            if (quantity > availableQuantity) {
                throw new RuntimeException("재고가 부족합니다. 최대 " + availableQuantity + "개까지 추가할 수 있습니다.");
            }
            Cart newCart = new Cart();
            newCart.setUsername(username);
            newCart.setProductNum(product.getProductNum());
            newCart.setProductName(product.getProductName());
            newCart.setProductPrice(product.getCost());
            newCart.setQuantity(quantity);
            newCart.setParkId(product.getParkId());

            cartRepository.save(newCart);
        }
    }

    public List<Cart> getCartProducts(String username) {
        return cartRepository.findByUsername(username);
    }

    public void updateQuantity(Long idx, int newQuantity) {
        Cart cart = cartRepository.findById(idx)
                .orElseThrow(() -> new RuntimeException("장바구니 항목을 찾을 수 없습니다. idx: " + idx));

        Product product = productRepository.findById(cart.getProduct().getIdx())
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다. 상품명: " + cart.getProduct().getProductName() + ", 공원 ID: " + cart.getParkId()));

        int availableQuantity = product.getQuantity();
        if (newQuantity > availableQuantity) {
            throw new RuntimeException("재고가 부족합니다. 최대 " + availableQuantity + "개까지 설정할 수 있습니다.");
        }

        cart.setQuantity(newQuantity);
        cartRepository.save(cart);
    }

    // 전체 장바구니 가격 계산 (username 인자를 받도록 수정)
    public Long calculateTotalPrice(String username) {
        List<Cart> cartProducts = cartRepository.findByUsername(username);

        return cartProducts.stream()
                .mapToLong(cart -> cart.getProductPrice() * cart.getQuantity())
                .sum();
    }

    // 전체 수량 업데이트 후 총 가격 반환
    public Long updateQuantitiesAndGetTotal(List<Cart> updatedCarts, String username) {
        for (Cart updatedCart : updatedCarts) {
            updateQuantity(updatedCart.getIdx(), updatedCart.getQuantity());
        }
        // 모든 업데이트가 끝난 후 전체 가격을 계산하여 반환
        return calculateTotalPrice(username);
    }

    public void deleteProductFromCart(Long idx) {
        Cart cart = cartRepository.findById(idx)
                .orElseThrow(() -> new RuntimeException("장바구니 항목을 찾을 수 없습니다. idx: " + idx));

        cartRepository.delete(cart);
    }

    // 예약 기능 추가 (Cart 항목을 기반으로 예약 생성)
    public void reserveProducts(String username) {
        List<Cart> cartProducts = cartRepository.findByUsername(username);

        if (cartProducts.isEmpty()) {
            throw new RuntimeException("장바구니가 비어 있습니다.");
        }

        // 예약 로직 예시 (실제 예약 처리 로직 추가 필요)
        cartProducts.forEach(cart -> {
            // 여기서 각 장바구니 항목에 대해 예약을 처리할 수 있습니다.
            System.out.println("예약 처리: " + cart.getProductName());
        });

        cartRepository.deleteAll(cartProducts);
    }

    public Cart findByIdxAndUsername(Long idx, String username) {
        Optional<Cart> cartOptional = cartRepository.findByIdxAndUsername(idx, username);
        if (cartOptional.isPresent()) {
            return cartOptional.get();
        } else {
            throw new RuntimeException("장바구니 항목을 찾을 수 없습니다. idx: " + idx + ", username: " + username);
        }
    }
}