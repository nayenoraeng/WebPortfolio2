package com.project.parkrental.cart;

import com.project.parkrental.parkList.model.Product;
import com.project.parkrental.security.DTO.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private Long idx;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "productNum", nullable = false)
    private String productNum;

    @Column(name = "productName", nullable = false)
    private String productName;

    @Column(name = "productPrice", nullable = false)
    private Long productPrice;

    @Column(name = "quantity", nullable = false, columnDefinition = "int default 1")
    private int quantity = 1;

    @Column(name = "parkId", nullable = false)
    private Long parkId;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "productName", referencedColumnName = "productName", insertable = false, updatable = false),
            @JoinColumn(name = "parkId", referencedColumnName = "parkId", insertable = false, updatable = false)
    })
    private Product product;
}
