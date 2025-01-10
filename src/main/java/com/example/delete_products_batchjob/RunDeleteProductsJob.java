package com.example.delete_products_batchjob;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class RunDeleteProductsJob implements CommandLineRunner {
    private final JobLauncher jobLauncher;
    private final Job deleteProductsJobConfig;
    @Autowired
    private ApplicationContext context;

    public RunDeleteProductsJob(JobLauncher jobLauncher, Job deleteProductsJobConfig) {
        this.jobLauncher = jobLauncher;
        this.deleteProductsJobConfig = deleteProductsJobConfig;
    }

    @Override
    public void run(String... args) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startTime", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(deleteProductsJobConfig, jobParameters);
            System.out.println("Job Status : " + execution.getStatus());
            System.out.println("Job completed");

            // Shutdown the application after job completion
            SpringApplication.exit(context);
        } catch (Exception e) {
            System.err.println("Job execution failed: " + e.getMessage());
            e.printStackTrace();
            SpringApplication.exit(context, () -> 1); // Exit with error code 1
        }
    }
}
