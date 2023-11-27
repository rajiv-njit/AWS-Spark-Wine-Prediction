package com.myproject.util;

import org.apache.spark.sql.SparkSession;

public class SparkUtils {

    public static SparkSession getSparkSession() {
        SparkSession spark = SparkSession.builder().getOrCreate();
        setAWSCredentials(spark);
        return spark;
    }

    public static void setAWSCredentials(SparkSession spark) {
        String accessKey = "ASIARK3QIYP3UPC3J542";
        String secretKey = "COFtmV4fzKUuAKaZlhIj6hgAh1MRLL2n0jkFzlGc";

        spark.conf().set("spark.hadoop.fs.s3a.access.key", accessKey);
        spark.conf().set("spark.hadoop.fs.s3a.secret.key", secretKey);
        spark.conf().set("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem");
        spark.conf().set("spark.hadoop.fs.s3a.multiobjectdelete.enable", "false");
        spark.conf().set("spark.hadoop.fs.s3a.connection.maximum", "1000");  // Updated line
    }
}


