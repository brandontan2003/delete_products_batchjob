package com.example.delete_products_batchjob.service.endpoint;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "product.service")
public class ProductServiceEndpointProperties {

    private String deleteProductEndpoint;

}
