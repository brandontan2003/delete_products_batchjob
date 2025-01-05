package com.example.delete_products_batchjob;

import com.example.delete_products_batchjob.batch.processor.InsertStagingRecordProcessor;
import com.example.delete_products_batchjob.batch.writer.InsertStagingRecordWriter;
import com.example.delete_products_batchjob.dto.StagingRequest;
import com.example.delete_products_batchjob.model.Product;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static com.example.delete_products_batchjob.constant.DeleteProductJobConstant.SELECT_PRODUCTS_TOBE_DELETED_SQL;
import static com.example.delete_products_batchjob.constant.ProductModelConstant.*;

public class DeleteProductsJob {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private Job job;
    @Autowired
    private DataSource productDataSource;

    @Bean
    public Job deleteProductsJob(JobRepository jobRepository, Step fetchFromProductTable) {
        return new JobBuilder("deleteProductsJob", jobRepository)
                .start(fetchFromProductTable)
                .build();
    }

    @Bean
    public Step fetchFromProductTable(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .<Product, StagingRequest>chunk(10, transactionManager)
                .reader(readProductToBeDeleted())
                .processor(insertStagingRecordProcessor())
                .writer(insertStagingRecordWriter())
                .build();
    }

    @Bean
    private InsertStagingRecordProcessor insertStagingRecordProcessor() {
        return new InsertStagingRecordProcessor();
    }

    @Bean
    public JdbcCursorItemReader<Product> readProductToBeDeleted() {
        return new JdbcCursorItemReaderBuilder<Product>()
                .dataSource(productDataSource)
                .sql(SELECT_PRODUCTS_TOBE_DELETED_SQL)
                .rowMapper((rs, rowNum) -> {
                    Product product = new Product();
                    product.setProductId(rs.getString(PRODUCT_ID));
                    product.setProductName(rs.getString(PRODUCT_NAME));
                    product.setProductDesc(rs.getString(PRODUCT_DESC));
                    product.setPrice(rs.getBigDecimal(PRICE));
                    product.setScheduledDeletionDate(rs.getDate(SCHEDULED_DELETION_DATE).toLocalDate());
                    return product;
                })
                .build();
    }

    @Bean
    public ItemWriter<StagingRequest> insertStagingRecordWriter() {
        return new InsertStagingRecordWriter();
    }
}
