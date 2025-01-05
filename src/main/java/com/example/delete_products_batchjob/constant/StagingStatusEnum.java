package com.example.delete_products_batchjob.constant;

import lombok.Getter;

@Getter
public enum StagingStatusEnum {
    APPROVED("APPROVED"),
    IN_PROGRESS("IN_PROGRESS"),
    FAILED("FAILED"),
    COMPLETED("COMPLETED");


    private final String value;

    StagingStatusEnum(String value) {
        this.value = value;
    }
}
