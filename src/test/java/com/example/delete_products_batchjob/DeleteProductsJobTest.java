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
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@ComponentScan(basePackages = "com.example.delete_products_batchjob")
@SpringBootTest(classes = {FlywayTestConfig.class})
class DeleteProductsJobTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StagingRepository stagingRepository;

    private final MockServerClient mockServerClient = new MockServerClient("localhost", 1011);

    private static final String VALID_PRODUCT1_ID = UUID.randomUUID().toString();
    private static final String VALID_PRODUCT2_ID = UUID.randomUUID().toString();
    private static final String VALID_PRODUCT3_ID = UUID.randomUUID().toString();
    private static final String INVALID_PRODUCT1_ID = UUID.randomUUID().toString();

    @BeforeEach
    public void before() throws IOException {
        // Start MockServer
        ClientAndServer mockServer = ClientAndServer.startClientAndServer(1011);

        // Set up MockServer expectation
        mockServerClient.when(
                request().withMethod("DELETE")
                        .withPath("/api/product/v1/delete")
                        .withHeader("Content-type", "application/json")
        ).respond(
                response().withStatusCode(200)
                        .withHeaders(new Header("Content-Type", "application/json; charset=utf-8"))
                        .withBody("{\"status\":\"SUCCESS\",\"result\":null}")
        );
    }

    @AfterEach
    public void tearDown() throws IOException {
        mockServerClient.close();
    }

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

    private static Product buildProduct(String productId, LocalDate deletionDate) {
        Product product = new Product();
        product.setProductId(productId);
        product.setProductName("Sample Product");
        product.setPrice(BigDecimal.valueOf(99));
        product.setScheduledDeletionDate(deletionDate);
        return product;
    }

    @Test
    void testJobExecution_Success() throws Exception {
        JobExecution jobExecution = jobLauncher.run(job, DEFAULT_JOB_PARAMETERS);
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    }

    private void saveProducts() {
        List<Product> products = new ArrayList<>();
        products.add(buildProduct(VALID_PRODUCT1_ID, LocalDate.now()));
        products.add(buildProduct(VALID_PRODUCT2_ID, LocalDate.now().minusDays(1)));
        products.add(buildProduct(VALID_PRODUCT3_ID, LocalDate.now().minusDays(10)));
        products.add(buildProduct(INVALID_PRODUCT1_ID, LocalDate.now().plusDays(1)));

        products.forEach(product -> productRepository.save(product));
    }

    @Test
    void testDeleteProductBatch_WithValidRecord_Success() throws JobInstanceAlreadyCompleteException,
            JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        saveProducts();
        assertEquals(4, productRepository.count());
        assertEquals(0, stagingRepository.count());

        JobExecution jobExecution = jobLauncher.run(job, DEFAULT_JOB_PARAMETERS);
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());

        assertEquals(3, stagingRepository.count());

        assertProductScheduledForTodayDeletion();
        assertProductScheduledForYesterdayDeletion();
        assertProductScheduledFor10DaysAgoDeletion();
    }

    private void assertProductScheduledForTodayDeletion() {
        Product product = productRepository.findByProductId(VALID_PRODUCT1_ID);
        Staging staging = stagingRepository.findByProductId(VALID_PRODUCT1_ID);
        assertProduct(staging, product);
    }

    private void assertProductScheduledForYesterdayDeletion() {
        Product product = productRepository.findByProductId(VALID_PRODUCT2_ID);
        Staging staging = stagingRepository.findByProductId(VALID_PRODUCT2_ID);
        assertProduct(staging, product);
    }

    private void assertProductScheduledFor10DaysAgoDeletion() {
        Product product = productRepository.findByProductId(VALID_PRODUCT3_ID);
        Staging staging = stagingRepository.findByProductId(VALID_PRODUCT3_ID);
        assertProduct(staging, product);
    }

    private void assertProduct(Staging staging, Product product) {
        assertEquals(staging.getProductId(), product.getProductId());
        assertEquals(staging.getProductName(), product.getProductName());
        assertEquals(staging.getScheduledDeletionDate(), product.getScheduledDeletionDate());
        assertEquals(StagingStatusEnum.COMPLETED.toString(), staging.getStatus());
        assertEquals(LocalDate.now(), staging.getCompletionDate());
    }
}
