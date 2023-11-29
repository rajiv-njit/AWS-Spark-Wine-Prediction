package com.myproject.config;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import software.amazon.awssdk.services.s3.S3Client;  // Use AWS SDK for Java V2
import com.myproject.data.DatasetLoader;

@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

    @Autowired
    private SparkConfig sparkConfig;

    @Autowired
    private AwsConfig awsConfig;

    @Autowired
    private DatasetLoader datasetLoader; // Inject DatasetLoader

    @Value("${spark.app.name}")
    private String appName;

    @Bean
    public SparkSession sparkSession() {
        try {
            LOGGER.info("Creating Spark Session...");
            SparkConf sparkConf = sparkConfig.sparkConf();

            SparkSession spark = sparkConfig.sparkSession(sparkConf);

            LOGGER.info("Spark Session created successfully.");

            return spark;
        } catch (Exception e) {
            LOGGER.error("Error creating Spark Session: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating Spark Session", e);
        }
    }

    @Bean
    public S3Client s3Client() {
        return awsConfig.s3Client();  // Use the updated method in AwsConfig
    }

    @Bean
    public Dataset<Row> trainingData(SparkSession spark, DatasetLoader datasetLoader) {
    try {
        LOGGER.info("Loading training data...");
        Dataset<Row> trainingData = datasetLoader.loadTrainingDataset(spark);
        LOGGER.info("Training data loaded successfully.");
        return trainingData;
    } catch (Exception e) {
        LOGGER.error("Error loading training data: {}", e.getMessage(), e);
        throw new RuntimeException("Error loading training data", e);
    }
}




    // Other beans and methods as needed
}
