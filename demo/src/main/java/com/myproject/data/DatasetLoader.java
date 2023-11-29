package com.myproject.data;  // Update with your actual package name

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DatasetLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatasetLoader.class);

    public static Dataset<Row> loadTrainingDataset(SparkSession spark) {
        // Load TrainingDataset.csv from AWS S3
        String trainingDataPath = "s3://s3-inputs-model-training/TrainingDataset.csv";
        LOGGER.info("Loading training dataset from: {}", trainingDataPath);
        Dataset<Row> trainingData = spark.read()
                .option("header", "true")
                .csv(trainingDataPath);
        LOGGER.info("Training dataset loaded successfully.");
        return trainingData;
    }

    public static Dataset<Row> loadValidationDataset(SparkSession spark) {
        // Load ValidationDataset.csv from AWS S3
        String validationDataPath = "s3://s3-inputs-model-training/ValidationDataset.csv";
        LOGGER.info("Loading validation dataset from: {}", validationDataPath);
        Dataset<Row> validationData = spark.read()
                .option("header", "true")
                .csv(validationDataPath);
        LOGGER.info("Validation dataset loaded successfully.");
        return validationData;
    }
}
