package com.myproject.config;

import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

    @Autowired
    private SparkConfig sparkConfig;

    @Autowired
    private AwsConfig awsConfig;

    @Bean
    public SparkSession sparkSession() {
        try {
            LOGGER.info("Creating Spark Session...");
            SparkSession spark = sparkConfig.sparkSession(sparkConfig.sparkConf()); // Fix: Pass the SparkConf to sparkSession method
            LOGGER.info("Spark Session created successfully.");

            // Any additional configurations related to SparkSession can be added here

            return spark;
        } catch (Exception e) {
            LOGGER.error("Error creating Spark Session: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating Spark Session", e);
        }
    }

    // Any additional beans and methods as needed
}
