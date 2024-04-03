package com.kh.coupang.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class Product {

    @Id
    @Column(name = "prod_code")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int prodCode;

    @Column(name = "prod_name")
    private String prodName;

    @Column
    private int price;

    @Column(name = "prod_photo")
    private String prodPhoto; // prod_photo

    @ManyToOne
    @JoinColumn(name="cate_code")
    private Category category;
}
