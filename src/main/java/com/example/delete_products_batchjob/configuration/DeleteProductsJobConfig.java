package com.example.delete_products_batchjob.configuration;

import com.example.delete_products_batchjob.batch.processor.InsertStagingRecordProcessor;
import com.example.delete_products_batchjob.batch.writer.InsertStagingRecordWriter;
import com.example.delete_products_batchjob.dto.StagingRequest;
import com.example.delete_products_batchjob.model.Product;
import com.example.delete_products_batchjob.repository.ProductRepository;
import com.example.delete_products_batchjob.repository.StagingRepository;
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
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import static com.example.delete_products_batchjob.constant.DeleteProductJobConstant.SELECT_PRODUCTS_TOBE_DELETED_SQL;
import static com.example.delete_products_batchjob.constant.ProductModelConstant.*;

@Configuration
@EnableJpaRepositories("com.example.delete_products_batchjob.repository")
public class DeleteProductsJobConfig {
    @Autowired
    @Qualifier("productJdbcTemplate")
    private JdbcTemplate productJdbcTemplate;

    @Autowired
    private StagingRepository stagingRepository;

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
    public InsertStagingRecordProcessor insertStagingRecordProcessor() {
        return new InsertStagingRecordProcessor();
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
    public ItemWriter<StagingRequest> insertStagingRecordWriter() {
        return new InsertStagingRecordWriter(stagingRepository);
    }
}
