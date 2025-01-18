package com.example.delete_products_batchjob;

import com.example.delete_products_batchjob.config.FlywayTestConfig;
import com.example.delete_products_batchjob.constant.StagingStatusEnum;
import com.example.delete_products_batchjob.model.Product;
import com.example.delete_products_batchjob.model.Staging;
import com.example.delete_products_batchjob.repository.ProductRepository;
import com.example.delete_products_batchjob.repository.StagingRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.stream.Stream;

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

    private static final MockServerClient mockServerClient = new MockServerClient("localhost", 1011);

    @BeforeAll
    static void before() {
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

    @AfterAll
    static void tearDown() {
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

    private static Product buildProduct(LocalDate deletionDate) {
        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
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

    static Stream<Arguments> testDeleteProductBatch_withValidRecord() {
        return Stream.of(
                Arguments.of("DeletionDate of product is today", buildProduct(LocalDate.now())),
                Arguments.of("DeletionDate of product is yesterday", buildProduct(LocalDate.now().minusDays(1))),
                Arguments.of("DeletionDate of product is 10 days ago", buildProduct(LocalDate.now().minusDays(10)))
        );
    }

    @ParameterizedTest
    @MethodSource("testDeleteProductBatch_withValidRecord")
    void testDeleteProductBatch_WithValidRecord_Success(String name, Product buildProduct) throws JobInstanceAlreadyCompleteException,
            JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        Product product = productRepository.save(buildProduct);
        assertEquals(1, productRepository.count());
        assertEquals(0, stagingRepository.count());

        JobExecution jobExecution = jobLauncher.run(job, DEFAULT_JOB_PARAMETERS);
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());

        assertEquals(1, stagingRepository.count());

        Staging staging = stagingRepository.findAll().get(0);
        assertEquals(staging.getProductId(), product.getProductId());
        assertEquals(staging.getProductName(), product.getProductName());
        assertEquals(staging.getScheduledDeletionDate(), product.getScheduledDeletionDate());
        assertEquals(StagingStatusEnum.COMPLETED.toString(), staging.getStatus());
        assertEquals(LocalDate.now(), staging.getCompletionDate());
    }

}
