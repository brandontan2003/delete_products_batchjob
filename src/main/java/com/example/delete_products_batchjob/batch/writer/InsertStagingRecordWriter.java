package com.example.delete_products_batchjob.batch.writer;

import com.example.delete_products_batchjob.dto.StagingRequest;
import com.example.delete_products_batchjob.model.Staging;
import com.example.delete_products_batchjob.repository.StagingRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InsertStagingRecordWriter implements ItemWriter<StagingRequest> {

    private final StagingRepository stagingRepository;

    public InsertStagingRecordWriter(StagingRepository stagingRepository) {
        this.stagingRepository = stagingRepository;
    }

    @Override
    public void write(Chunk<? extends StagingRequest> chunk) {
        List<? extends StagingRequest> stagingRequests = chunk.getItems();

        // Example: Loop through each item and insert it
        for (StagingRequest request : stagingRequests) {
            Staging staging = new Staging();
            staging.setProductId(request.getProductId());
            staging.setProductName(request.getProductName());
            staging.setStatus(request.getStatus());
            staging.setScheduledDeletionDate(request.getScheduledDeletionDate());
            stagingRepository.saveAndFlush(staging);
        }
    }
}
