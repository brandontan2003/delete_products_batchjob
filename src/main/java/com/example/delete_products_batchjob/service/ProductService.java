package com.example.delete_products_batchjob.service;

import com.example.delete_products_batchjob.dto.product.DeleteProductRequest;
import com.example.delete_products_batchjob.dto.product.ResponsePayload;
import com.example.delete_products_batchjob.service.endpoint.ProductServiceEndpointProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProductService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProductServiceEndpointProperties endpointProperties;

    private static final ParameterizedTypeReference<ResponsePayload<String>> DELETE_PRODUCT_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };


    public <T> ResponseEntity<T> callApi(String url, HttpMethod method, Object requestBody,
                         ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);

        return restTemplate.exchange(
                url,
                method,
                requestEntity,
                responseType
        );
    }

    public ResponseEntity<ResponsePayload<String>> deleteProductApi(DeleteProductRequest request) {
        return callApi(endpointProperties.getDeleteProductEndpoint(), HttpMethod.DELETE, request,
                DELETE_PRODUCT_TYPE_REFERENCE);
    }

}
