package com.example.delete_products_batchjob.dto;

import com.example.delete_products_batchjob.dto.product.DeleteProductRequest;
import com.example.delete_products_batchjob.dto.staging.UpdateStagingRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteProductWriterRequest {

    private DeleteProductRequest deleteProductRequest;
    private UpdateStagingRequest updateStagingRequest;

}
