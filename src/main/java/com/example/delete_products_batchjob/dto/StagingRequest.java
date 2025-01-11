package com.example.delete_products_batchjob.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StagingRequest {

    private String productId;
    private String productName;
    private String status;
    private LocalDate scheduledDeletionDate;

}
