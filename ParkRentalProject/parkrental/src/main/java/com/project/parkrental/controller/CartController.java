package com.project.parkrental.controller;

import com.project.parkrental.cart.Cart;
import com.project.parkrental.cart.CartService;
import com.project.parkrental.parkList.model.Product;
import com.project.parkrental.parkList.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public String addProductToCart(
            @RequestParam("idx[]") List<Long> idxList,
            Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            for (Long productIdx : idxList) {
                Product product = productService.getProductById(productIdx);

                if (product.getParkList() == null) {
                    model.addAttribute("error", "공원 정보가 없습니다.");
                    return "redirect:/user/cart";
                }

                cartService.addProductToCart(username, product, 1);  // 수량을 기본 1로 설정
            }

            return "redirect:/user/cart";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "redirect:/user/cart";
        }
    }

    @GetMapping
    public String getCartProducts(Model model, Principal principal) {
        String username = principal.getName(); // 로그인된 사용자 이름 가져오기
        List<Cart> cartProducts = cartService.getCartProducts(username);

        Long totalPrice = cartProducts.stream()
                .mapToLong(cart -> cart.getProductPrice() * cart.getQuantity())
                .sum();

        model.addAttribute("cartProducts", cartProducts);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("username", username);

        return "user/cart";
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, String>> updateQuantity(
            @RequestBody List<Map<String, Object>> payload) {
        Map<String, String> response = new HashMap<>();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            for (Map<String, Object> item : payload) {
                Long idx = Long.parseLong(item.get("idx").toString());
                int quantity = Integer.parseInt(item.get("quantity").toString());

                cartService.updateQuantity(idx, quantity);
            }

            Long newTotalPrice = cartService.calculateTotalPrice(username);
            response.put("newTotalPrice", String.valueOf(newTotalPrice));
            response.put("success", "수량이 변경되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/delete/{idx}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long idx) {
        Map<String, String> response = new HashMap<>();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Cart cart = cartService.findByIdxAndUsername(idx, username);
            if (cart != null) {
                cartService.deleteProductFromCart(idx);
                response.put("success", "물품이 삭제되었습니다.");
            } else {
                response.put("error", "해당 항목을 삭제할 권한이 없습니다.");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "물품을 삭제하는 도중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 사용자 이름을 가져와 JavaScript에서 사용할 수 있도록 전달
    @GetMapping("/cart")
    public String showCart(Model model, Principal principal) {
        String username = principal.getName(); // 로그인한 사용자 이름 가져오기
        model.addAttribute("username", username); // 사용자 이름을 Thymeleaf로 전달
        return "cart";
    }
}
