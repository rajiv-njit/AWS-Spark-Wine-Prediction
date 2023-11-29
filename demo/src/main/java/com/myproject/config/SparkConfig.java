package com.myproject.config;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfig {

    @Value("${spark.app.name}")
    private String appName;

    @Value("${spark.master}")
    private String master;

    @Value("${aws_access_key_id:#{null}}")
    private String accessKeyId;

    @Value("${aws_secret_access_key:#{null}}")
    private String secretKey;

    @Bean
    public SparkConf sparkConf() {
        SparkConf sparkConf = new SparkConf()
                .setAppName(appName)
                .setMaster(master)
                .set("spark.driver.bindAddress", "127.0.0.1")
                .set("spark.driver.port", "7077");

        if (accessKeyId != null && secretKey != null) {
            // If explicit credentials are provided, set them for S3
            sparkConf.set("spark.hadoop.fs.s3a.access.key", accessKeyId)
                     .set("spark.hadoop.fs.s3a.secret.key", secretKey);
        }

        return sparkConf;
    }

    @Bean
    public SparkSession sparkSession(SparkConf sparkConf) {
        return SparkSession.builder()
                .config(sparkConf)
                .getOrCreate();
    }
}
