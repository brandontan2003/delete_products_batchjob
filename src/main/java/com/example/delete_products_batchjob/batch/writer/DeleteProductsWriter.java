package com.example.delete_products_batchjob.batch.writer;

import com.example.delete_products_batchjob.constant.StagingStatusEnum;
import com.example.delete_products_batchjob.dto.DeleteProductWriterRequest;
import com.example.delete_products_batchjob.dto.staging.UpdateStagingRequest;
import com.example.delete_products_batchjob.model.Staging;
import com.example.delete_products_batchjob.repository.StagingRepository;
import com.example.delete_products_batchjob.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class DeleteProductsWriter implements ItemWriter<DeleteProductWriterRequest> {

    private final StagingRepository stagingRepository;
    private final ProductService productService;

    public DeleteProductsWriter(StagingRepository stagingRepository, ProductService productService) {
        this.stagingRepository = stagingRepository;
        this.productService = productService;
    }

    @Override
    public void write(Chunk<? extends DeleteProductWriterRequest> chunk) {
        List<? extends DeleteProductWriterRequest> writerRequests = chunk.getItems();

        for (DeleteProductWriterRequest request : writerRequests) {
            UpdateStagingRequest updateStagingRequest = request.getUpdateStagingRequest();
            try {
                productService.deleteProductApi(request.getDeleteProductRequest());
                updateStagingRequest.setStatus(StagingStatusEnum.COMPLETED.getValue());
            } catch (HttpClientErrorException | HttpServerErrorException ex) {
                updateStagingRequest.setStatus(StagingStatusEnum.FAILED.getValue());
                log.error("Exception occurred while calling API :::: {}", ex.getResponseBodyAsString());
            }
            updateStagingRecord(updateStagingRequest);
        }
    }

    private void updateStagingRecord(UpdateStagingRequest updateStagingRequest) {
        String stagingStatus = updateStagingRequest.getStatus();
        Staging staging = stagingRepository.findByStagingId(updateStagingRequest.getStagingId());
        staging.setStatus(stagingStatus);
        if (StagingStatusEnum.COMPLETED.getValue().equalsIgnoreCase(stagingStatus)) {
            staging.setCompletionDate(LocalDate.now());
        }
        stagingRepository.saveAndFlush(staging);
    }
}
