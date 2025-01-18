package com.example.delete_products_batchjob.repository;

import com.example.delete_products_batchjob.model.Staging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StagingRepository extends JpaRepository<Staging, String> {

    Staging findByStagingId(String stagingId);
    Staging findByProductId(String productId);

}
