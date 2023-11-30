package com.myproject;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.myproject.ml.*;

@SpringBootApplication
@ComponentScan(basePackages = "com.myproject")
public class Application implements CommandLineRunner {

    @Autowired
    private WineQualityPredictor wineQualityPredictor;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        // Initialize Spark
        SparkSession sparkSession = SparkSession.builder().appName("WineQualityPrediction").master("local[*]").getOrCreate();

        try {
            // Load your training data
            Dataset<Row> trainingData = sparkSession.read().format("csv").option("header", "true").option("inferSchema", "true").load("path/to/training_data.csv");

            // Train the model
            wineQualityPredictor.trainModel(trainingData);

            // Load your test data
            Dataset<Row> testData = sparkSession.read().format("csv").option("header", "true").option("inferSchema", "true").load("path/to/test_data.csv");

            // Make predictions
            Dataset<Row> predictions = wineQualityPredictor.predict(testData);

            // Show the predictions
            predictions.show();
        } finally {
            // Stop Spark
            sparkSession.stop();
        }
    }
}
