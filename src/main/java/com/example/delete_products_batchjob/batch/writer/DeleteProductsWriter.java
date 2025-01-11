package com.example.delete_products_batchjob.batch.writer;

import com.example.delete_products_batchjob.constant.StagingStatusEnum;
import com.example.delete_products_batchjob.dto.DeleteProductWriterRequest;
import com.example.delete_products_batchjob.dto.product.ResponsePayload;
import com.example.delete_products_batchjob.dto.staging.UpdateStagingRequest;
import com.example.delete_products_batchjob.model.Staging;
import com.example.delete_products_batchjob.repository.StagingRepository;
import com.example.delete_products_batchjob.service.ProductService;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
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
            ResponseEntity<ResponsePayload<String>> response =
                    productService.deleteProductApi(request.getDeleteProductRequest());
            UpdateStagingRequest updateStagingRequest = request.getUpdateStagingRequest();
            if (response.getStatusCode().isError()) {
                updateStagingRequest.setStatus(StagingStatusEnum.FAILED.getValue());
            }
            updateStagingRequest.setStatus(StagingStatusEnum.COMPLETED.getValue());
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
