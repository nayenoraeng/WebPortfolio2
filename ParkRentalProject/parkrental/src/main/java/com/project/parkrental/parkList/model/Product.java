package com.project.parkrental.parkList.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Optional;

@Data
@Entity
@Table(name = "product", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"productName", "parkId"})
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private Long idx;

    @Column(name = "productNum", nullable = false, length = 10)
    private String productNum;

    @Column(name = "businessName", nullable = false, length = 100)
    private String businessName;

    @Column(name = "productName", nullable = false, length = 50)
    private String productName;

    @Column(name = "quantity", nullable = false, columnDefinition = "int default 1")
    private int quantity = 1;

    @Column(name = "cost")
    private Long cost;

    @ManyToOne
    @JoinColumn(name = "parkId", nullable = false)
    private ParkList parkList;

    @Column(name = "parkId", insertable = false, updatable = false)
    private Long parkId;

    public Product() {}

    public Product(Long idx, String productNum, String businessName, String productName, int quantity, Long cost, ParkList parkList) {
        this.idx = idx;
        this.productNum = productNum;
        this.businessName = businessName;
        this.productName = productName;
        this.quantity = quantity;
        this.cost = cost;
        this.parkList = parkList;
    }

    // parkId 값을 반환하는 getter
    public Long getParkId() {
        return Optional.ofNullable(this.parkList)
                .map(ParkList::getParkId)
                .orElse(null);  // parkList가 null일 경우 null 반환
    }
}
