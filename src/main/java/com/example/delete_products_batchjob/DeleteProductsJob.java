package com.example.delete_products_batchjob;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;

public class DeleteProductsJob {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;



}
