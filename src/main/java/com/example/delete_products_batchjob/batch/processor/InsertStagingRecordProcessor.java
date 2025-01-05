package com.example.delete_products_batchjob.batch.processor;

import com.example.delete_products_batchjob.constant.StagingStatusEnum;
import com.example.delete_products_batchjob.dto.StagingRequest;
import com.example.delete_products_batchjob.model.Product;
import org.springframework.batch.item.ItemProcessor;


public class InsertStagingRecordProcessor implements ItemProcessor<Product, StagingRequest> {

    @Override
    public StagingRequest process(Product product) throws Exception {
        return getStagingRequest(product);
    }

    private static StagingRequest getStagingRequest(Product product) {
        StagingRequest stagingRequest = new StagingRequest();
        stagingRequest.setProductId(product.getProductId());
        stagingRequest.setStatus(StagingStatusEnum.IN_PROGRESS.getValue());
        stagingRequest.setScheduledDeletionDate(product.getScheduledDeletionDate());
        return stagingRequest;
    }

}
