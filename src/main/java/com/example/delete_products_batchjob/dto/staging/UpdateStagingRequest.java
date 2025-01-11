package com.example.delete_products_batchjob.dto.staging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStagingRequest {

    private String stagingId;
    private String status;
}
