package com.example.delete_products_batchjob;

import com.example.delete_products_batchjob.config.FlywayTestConfig;
import com.example.delete_products_batchjob.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ComponentScan(basePackages = "com.example.delete_products_batchjob")
@SpringBootTest(classes = {FlywayTestConfig.class})
class DeleteProductsJobTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Autowired
    private ProductRepository productRepository;

    private final JobParameters DEFAULT_JOB_PARAMETERS = new JobParametersBuilder()
            .addLong("startTime", System.currentTimeMillis())
            .toJobParameters();

    @Test
    void testJobExecution_Success() throws Exception {
        JobExecution jobExecution = jobLauncher.run(job, DEFAULT_JOB_PARAMETERS);
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    }

}
