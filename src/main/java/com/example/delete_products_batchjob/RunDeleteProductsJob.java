package com.example.delete_products_batchjob;

import jakarta.annotation.PostConstruct;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class RunDeleteProductsJob {
    private final JobLauncher jobLauncher;
    private final Job deleteProductsJobConfig;

    public RunDeleteProductsJob(JobLauncher jobLauncher, Job deleteProductsJobConfig) {
        this.jobLauncher = jobLauncher;
        this.deleteProductsJobConfig = deleteProductsJobConfig;
    }

    @PostConstruct
    public void runBatchJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startTime", System.currentTimeMillis())
                .toJobParameters();
        JobExecution execution = jobLauncher.run(deleteProductsJobConfig, jobParameters);
        System.out.println("Job Status : " + execution.getStatus());
        System.out.println("Job completed");
    }

}
