// SparkConfig.java
package com.myproject.config;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfig {

    @Bean
    public SparkSession sparkSession() {
        SparkConf sparkConf = new SparkConf()
                .setAppName("my-spark-app")
                .setMaster("local")
                .set("spark.driver.bindAddress", "192.168.1.222"); // Set the appropriate binding address

        return SparkSession.builder()
                .config(sparkConf)
                .getOrCreate();
    }
}
