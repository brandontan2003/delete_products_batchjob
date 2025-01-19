package com.example.delete_products_batchjob.service;

import com.example.delete_products_batchjob.dto.product.DeleteProductRequest;
import com.example.delete_products_batchjob.service.endpoint.ProductServiceEndpointProperties;
import com.example.product_common_core.dto.ResponsePayload;
import com.example.product_common_core.service.RestCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private RestCallService restCallService;
    @Autowired
    private ProductServiceEndpointProperties endpointProperties;

    private static final ParameterizedTypeReference<ResponsePayload<String>> DELETE_PRODUCT_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };

    public ResponseEntity<ResponsePayload<String>> deleteProductApi(DeleteProductRequest request) {
        return restCallService.fetchApiResponseEntity(endpointProperties.getDeleteProductEndpoint(),
                HttpMethod.DELETE, request, DELETE_PRODUCT_TYPE_REFERENCE);
    }

}
