package com.example.delete_products_batchjob.constant;

public class DeleteProductJobConstant {

    public static final Integer CHUCK_SIZE = 10;
    public static final String DELETE_PRODUCTS_JOB = "deleteProductsJob";
    public static final String FETCH_PRODUCTS_FOR_DELETION = "fetchProductsForDeletion";

    public static final String SELECT_PRODUCTS_TOBE_DELETED_SQL
            = "SELECT product_id, product_name, product_desc, price, scheduled_deletion_date from product_table where"
            + " scheduled_deletion_date <= now()";

}
