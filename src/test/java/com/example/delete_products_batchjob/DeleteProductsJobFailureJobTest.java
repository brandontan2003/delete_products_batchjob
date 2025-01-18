package com.example.delete_products_batchjob;

import com.example.delete_products_batchjob.config.FlywayTestConfig;
import com.example.delete_products_batchjob.constant.StagingStatusEnum;
import com.example.delete_products_batchjob.model.Product;
import com.example.delete_products_batchjob.model.Staging;
import com.example.delete_products_batchjob.repository.ProductRepository;
import com.example.delete_products_batchjob.repository.StagingRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ComponentScan(basePackages = "com.example.delete_products_batchjob")
@SpringBootTest(classes = {FlywayTestConfig.class})
class DeleteProductsJobFailureJobTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StagingRepository stagingRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        stagingRepository.deleteAll();
    }

    @AfterEach
    void cleanUp() {
        productRepository.deleteAll();
        stagingRepository.deleteAll();
    }

    private final JobParameters DEFAULT_JOB_PARAMETERS = new JobParametersBuilder().addLong("startTime",
            System.currentTimeMillis()).toJobParameters();

    private static Product buildProduct(LocalDate deletionDate) {
        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setProductName("Sample Product");
        product.setPrice(BigDecimal.valueOf(99));
        product.setScheduledDeletionDate(deletionDate);
        return product;
    }

    @Test
    void testDeleteProductBatch_ApiConnectionFailure_Error() throws JobInstanceAlreadyCompleteException,
            JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        Product product = productRepository.save(buildProduct(LocalDate.now()));
        assertEquals(1, productRepository.count());
        assertEquals(0, stagingRepository.count());

        JobExecution jobExecution = jobLauncher.run(job, DEFAULT_JOB_PARAMETERS);
        assertEquals(ExitStatus.FAILED.getExitCode(), jobExecution.getExitStatus().getExitCode());

        assertEquals(1, stagingRepository.count());
        Staging staging = stagingRepository.findAll().get(0);
        assertEquals(staging.getProductId(), product.getProductId());
        assertEquals(staging.getProductName(), product.getProductName());
        assertEquals(staging.getScheduledDeletionDate(), product.getScheduledDeletionDate());
        assertEquals(StagingStatusEnum.IN_PROGRESS.toString(), staging.getStatus());
        assertNull(staging.getCompletionDate());
    }

}
