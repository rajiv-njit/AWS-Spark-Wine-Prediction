package com.myproject;

import com.myproject.config.AWSConfig;
import com.myproject.ml.*;
import com.myproject.util.SparkUtils;

import software.amazon.awssdk.services.s3.S3Client;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;

@SpringBootApplication
@ComponentScan(basePackages = "com.myproject")
public class Application {

    @Autowired
    @Lazy
    private WineQualityPredictor wineQualityPredictor;
    @Autowired
    @Lazy
    private LinearRegressionModel linearRegressionModel;
    @Autowired
    @Lazy
    private LogisticRegressionModel logisticRegressionModel;

    @Autowired
    private SparkUtils sparkUtils;

     @Autowired
    private AWSConfig awsConfig;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(S3Client s3Client, SparkSession spark) {
        return args -> {
            System.out.println("Application Started...");

            // Update SparkUtils to set the S3Client
            sparkUtils.setS3Client(s3Client);

            // Use SparkUtils to get the SparkSession
            SparkSession sparkSession = sparkUtils.getOrCreateSparkSession();

            // Load the training dataset
            Dataset<Row> trainingData = sparkSession.read().format("csv").option("header", "true").load("path_to_training_data.csv");

            // Train all the models
            wineQualityPredictor.trainModels(trainingData);

            // Use the trained models for prediction
            Dataset<Row> testData = sparkSession.read().format("csv").option("header", "true").load("path_to_test_data.csv");

            Dataset<Row> regressionPredictions = wineQualityPredictor.predictRegression(testData);
            Dataset<Row> linearRegressionPredictions = wineQualityPredictor.predictLinearRegression(testData);
            Dataset<Row> logisticRegressionPredictions = wineQualityPredictor.predictLogisticRegression(testData);

            // Display the predictions or perform other actions
            regressionPredictions.show();
            linearRegressionPredictions.show();
            logisticRegressionPredictions.show();

            System.out.println("Application Completed.");
        };
    }

    @Bean
    public SparkSession sparkSession() {
        return sparkUtils.getOrCreateSparkSession();
    }
}
