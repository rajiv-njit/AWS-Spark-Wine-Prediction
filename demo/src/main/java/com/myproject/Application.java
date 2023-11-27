package com.myproject;

import com.myproject.ml.*;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.myproject")
public class Application {

    @Autowired
    private WineQualityPredictor wineQualityPredictor;
    @Autowired
    private LinearRegressionModel linearRegressionModel;
    @Autowired
    private LogisticRegressionModel logisticRegressionModel;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(SparkSession spark) {
        return args -> {
            // Load the training dataset
            Dataset<Row> trainingData = spark.read().format("csv").option("header", "true").load("path_to_training_data.csv");

            // Train all the models
            wineQualityPredictor.trainModels(trainingData);

            // Use the trained models for prediction
            Dataset<Row> testData = spark.read().format("csv").option("header", "true").load("path_to_test_data.csv");

            Dataset<Row> regressionPredictions = wineQualityPredictor.predictRegression(testData);
            Dataset<Row> linearRegressionPredictions = wineQualityPredictor.predictLinearRegression(testData);
            Dataset<Row> logisticRegressionPredictions = wineQualityPredictor.predictLogisticRegression(testData);

            // Display the predictions or perform other actions
            regressionPredictions.show();
            linearRegressionPredictions.show();
            logisticRegressionPredictions.show();
        };
    }

    @Bean
    public SparkSession sparkSession() {
        // Your SparkSession bean definition, similar to what you did in SparkConfig
        SparkSession sparkSession = SparkSession.builder()
                .appName("my-spark-app")
                .master("local")
                .config("spark.driver.bindAddress", "192.168.1.222")
                .getOrCreate();

        return sparkSession;
    }
}
