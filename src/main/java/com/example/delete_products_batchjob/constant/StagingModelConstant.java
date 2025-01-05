package com.example.delete_products_batchjob.constant;

public class StagingModelConstant {

    public static final String STAGING_TABLE = "staging_table";
    public static final String STAGING_ID = "staging_id";
    public static final String PRODUCT_ID = "product_id";
    public static final String STATUS = "status";
    public static final String SCHEDULED_DELETION_DATE = "scheduled_deletion_date";


    public static class FieldLength {
        public static final int PRODUCT_ID = 36;
        public static final int STAGING_ID = 36;
        public static final int STATUS = 15;
    }
}
