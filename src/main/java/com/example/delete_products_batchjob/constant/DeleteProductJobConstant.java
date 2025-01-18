package com.example.delete_products_batchjob.constant;

public class DeleteProductJobConstant {

    public static final Integer CHUCK_SIZE = 10;
    public static final Integer SKIP_LIMIT = 2;
    public static final Integer RETRY_LIMIT = 3;
    public static final String DELETE_PRODUCTS_JOB = "deleteProductsJob";
    public static final String FETCH_PRODUCTS_FOR_DELETION = "fetchProductsForDeletion";
    public static final String CALL_DELETE_PRODUCT_API = "callDeleteProductApi";

    public static final String SELECT_PRODUCTS_TOBE_DELETED_SQL
            = "SELECT product_id, product_name, product_desc, price, scheduled_deletion_date from product_table where"
            + " scheduled_deletion_date <= now()";

    public static final String SELECT_RECORDS_FROM_STAGING_SQL
            = "SELECT staging_id, product_id, product_name, status, scheduled_deletion_date from staging_table where "
            + "status = 'IN_PROGRESS'";
}
