package com.example.delete_products_batchjob.configuration;

import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Configuration
public class ApplicationConfig {

    @Bean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder() {
        JpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        return new EntityManagerFactoryBuilder(jpaVendorAdapter, new HashMap<>(), null);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
