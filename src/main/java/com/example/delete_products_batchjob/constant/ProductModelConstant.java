package com.example.delete_products_batchjob.constant;

public class ProductModelConstant {

    public static final String PRODUCT_TABLE = "product_table";
    public static final String PRODUCT_ID = "product_id";
    public static final String PRODUCT_NAME = "product_name";
    public static final String PRODUCT_DESC = "product_desc";
    public static final String PRICE = "price";
    public static final String SCHEDULED_DELETION_DATE = "scheduled_deletion_date";


    public static class FieldLength {
        public static final int PRODUCT_ID = 36;
        public static final int PRODUCT_NAME = 100;
        public static final int PRODUCT_DESC = 350;
    }
}
