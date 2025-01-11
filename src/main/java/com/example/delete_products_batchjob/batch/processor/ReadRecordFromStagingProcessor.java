package com.example.delete_products_batchjob.batch.processor;

import com.example.delete_products_batchjob.dto.DeleteProductWriterRequest;
import com.example.delete_products_batchjob.dto.product.DeleteProductRequest;
import com.example.delete_products_batchjob.dto.staging.UpdateStagingRequest;
import com.example.delete_products_batchjob.model.Staging;
import lombok.NonNull;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class ReadRecordFromStagingProcessor implements ItemProcessor<Staging, DeleteProductWriterRequest> {

    @NonNull
    @Override
    public DeleteProductWriterRequest process(Staging staging) {
        DeleteProductWriterRequest writerRequest = new DeleteProductWriterRequest();
        writerRequest.setDeleteProductRequest(getDeleteProductRequest(staging.getProductId()));
        writerRequest.setUpdateStagingRequest(getUpdateStagingRequest(staging));
        return writerRequest;
    }

    private static DeleteProductRequest getDeleteProductRequest(String productId) {
        return DeleteProductRequest.builder().productId(productId).build();
    }

    private static UpdateStagingRequest getUpdateStagingRequest(Staging staging) {
        return UpdateStagingRequest.builder().stagingId(staging.getStagingId()).status(staging.getStatus()).build();
    }
}
