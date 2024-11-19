package com.project.parkrental.cart;

import com.project.parkrental.parkList.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUsername(String username);

    Cart findByUsernameAndProduct(String username, Product product);

    Cart findByUsernameAndProductNameAndParkId(String username, String productName, Long parkId);

    Optional<Cart> findByProductIdx(Long productIdx);

    Optional<Cart> findByIdxAndUsername(Long idx, String username);
}
