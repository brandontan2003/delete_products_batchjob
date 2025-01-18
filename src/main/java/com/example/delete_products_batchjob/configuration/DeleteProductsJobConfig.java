package com.example.delete_products_batchjob.configuration;

import com.example.delete_products_batchjob.batch.processor.InsertStagingRecordProcessor;
import com.example.delete_products_batchjob.batch.processor.ReadRecordFromStagingProcessor;
import com.example.delete_products_batchjob.batch.writer.DeleteProductsWriter;
import com.example.delete_products_batchjob.batch.writer.InsertStagingRecordWriter;
import com.example.delete_products_batchjob.dto.DeleteProductWriterRequest;
import com.example.delete_products_batchjob.dto.StagingRequest;
import com.example.delete_products_batchjob.model.Product;
import com.example.delete_products_batchjob.model.Staging;
import com.example.delete_products_batchjob.repository.StagingRepository;
import com.example.delete_products_batchjob.service.ProductService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import static com.example.delete_products_batchjob.constant.DeleteProductJobConstant.*;
import static com.example.delete_products_batchjob.constant.ProductModelConstant.*;
import static com.example.delete_products_batchjob.constant.StagingModelConstant.STAGING_ID;
import static com.example.delete_products_batchjob.constant.StagingModelConstant.STATUS;

@Configuration
@EnableJpaRepositories("com.example.delete_products_batchjob.repository")
public class DeleteProductsJobConfig {
    @Autowired
    @Qualifier("productJdbcTemplate")
    private JdbcTemplate productJdbcTemplate;

    @Autowired
    @Qualifier("stagingJdbcTemplate")
    private JdbcTemplate stagingJdbcTemplate;

    @Autowired
    private StagingRepository stagingRepository;

    @Autowired
    private ProductService productService;

    @Bean
    public Job deleteProductsJob(JobRepository jobRepository, Step fetchFromProductTable, Step executeDeletionRequest) {
        return new JobBuilder(DELETE_PRODUCTS_JOB, jobRepository)
                .start(fetchFromProductTable)
                .next(executeDeletionRequest)
                .build();
    }

    @Bean
    public Step fetchFromProductTable(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder(FETCH_PRODUCTS_FOR_DELETION, jobRepository)
                .<Product, StagingRequest>chunk(CHUCK_SIZE, transactionManager)
                .reader(readProductToBeDeleted())
                .processor(insertStagingRecordProcessor())
                .writer(insertStagingRecordWriter())
                .faultTolerant()
                .retry(RecoverableDataAccessException.class)
                .retryLimit(RETRY_LIMIT)
                .skipLimit(SKIP_LIMIT)
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Product> readProductToBeDeleted() {
        return new JdbcCursorItemReaderBuilder<Product>()
                .name("productReader")
                .dataSource(productJdbcTemplate.getDataSource())
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
    public InsertStagingRecordProcessor insertStagingRecordProcessor() {
        return new InsertStagingRecordProcessor();
    }

    @Bean
    public ItemWriter<StagingRequest> insertStagingRecordWriter() {
        return new InsertStagingRecordWriter(stagingRepository);
    }

    @Bean
    public Step executeDeletionRequest(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder(CALL_DELETE_PRODUCT_API, jobRepository)
                .<Staging, DeleteProductWriterRequest>chunk(CHUCK_SIZE, transactionManager)
                .reader(readRecordsFromStagingTable())
                .processor(readRecordsFromStagingProcessor())
                .writer(deleteProductsWriter())
                .faultTolerant()
                .retry(RecoverableDataAccessException.class)
                .retryLimit(RETRY_LIMIT)
                .skipLimit(SKIP_LIMIT)
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Staging> readRecordsFromStagingTable() {
        return new JdbcCursorItemReaderBuilder<Staging>()
                .name("stagingReader")
                .dataSource(stagingJdbcTemplate.getDataSource())
                .sql(SELECT_RECORDS_FROM_STAGING_SQL)
                .rowMapper((rs, rowNum) -> {
                    Staging staging = new Staging();
                    staging.setStagingId(rs.getString(STAGING_ID));
                    staging.setProductId(rs.getString(PRODUCT_ID));
                    staging.setProductName(rs.getString(PRODUCT_NAME));
                    staging.setStatus(rs.getString(STATUS));
                    staging.setScheduledDeletionDate(rs.getDate(SCHEDULED_DELETION_DATE).toLocalDate());
                    return staging;
                })
                .build();
    }

    @Bean
    public ReadRecordFromStagingProcessor readRecordsFromStagingProcessor() {
        return new ReadRecordFromStagingProcessor();
    }

    @Bean
    public ItemWriter<DeleteProductWriterRequest> deleteProductsWriter() {
        return new DeleteProductsWriter(stagingRepository, productService);
    }
}
