package com.example.delete_products_batchjob.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.example.delete_products_batchjob.constant.ProductModelConstant.*;


@Data
@Entity
@Table(name = PRODUCT_TABLE)
public class Product {

    @Id
    @Column(name = PRODUCT_ID, length = FieldLength.PRODUCT_ID)
    private String productId;
    @Column(name = PRODUCT_NAME, length = FieldLength.PRODUCT_NAME, nullable = false)
    private String productName;
    @Column(name = PRODUCT_DESC, length = FieldLength.PRODUCT_DESC)
    private String productDesc;
    @Column(name = PRICE, nullable = false)
    private BigDecimal price;
    @Column(name = SCHEDULED_DELETION_DATE)
    private LocalDate scheduledDeletionDate;
}
