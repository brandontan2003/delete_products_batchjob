package com.example.delete_products_batchjob.constant;

public class DeleteProductJobConstant {

    public static final String SELECT_PRODUCTS_TOBE_DELETED_SQL
            = "SELECT product_id, product_name, product_desc, price, scheduled_deletion_date from product_table where"
            + " scheduled_deletion_date <= now()";

}
