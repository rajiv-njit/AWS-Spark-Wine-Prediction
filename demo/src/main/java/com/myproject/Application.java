package com.myproject;

import com.myproject.config.AppConfig;
import com.myproject.ml.LinearRegressionModel;
import com.myproject.ml.LogisticRegressionModel;
import com.myproject.ml.WineQualityPredictor;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import software.amazon.awssdk.services.s3.S3Client;

@SpringBootApplication
@ComponentScan(basePackages = "com.myproject")
public class Application {

    @Autowired
    private WineQualityPredictor wineQualityPredictor;

    @Autowired
    private LinearRegressionModel linearRegressionModel;

    @Autowired
    private LogisticRegressionModel logisticRegressionModel;

    @Autowired
    private AppConfig appConfig;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(S3Client s3Client, SparkSession spark) {
        return args -> {
            System.out.println("Application Started...");

            // Use AppConfig to set the S3Client
            appConfig.setS3Client(s3Client);

            // Use AppConfig to get the SparkSession
            SparkSession sparkSession = appConfig.sparkSession();

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
}
