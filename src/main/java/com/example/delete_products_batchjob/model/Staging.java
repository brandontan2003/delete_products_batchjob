package com.example.delete_products_batchjob.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

import static com.example.delete_products_batchjob.constant.StagingModelConstant.*;


@Data
@Entity
@Table(name = STAGING_TABLE)
public class Staging {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = STAGING_ID, length = FieldLength.STAGING_ID)
    private String stagingId;
    @Column(name = PRODUCT_ID, length = FieldLength.PRODUCT_ID, nullable = false)
    private String productId;
    @Column(name = PRODUCT_NAME, length = FieldLength.PRODUCT_NAME, nullable = false)
    private String productName;
    @Column(name = STATUS, length = FieldLength.STATUS, nullable = false)
    private String status;
    @Column(name = SCHEDULED_DELETION_DATE, nullable = false)
    private LocalDate scheduledDeletionDate;
    @Column(name = COMPLETION_DATE)
    private LocalDate completionDate;
}
