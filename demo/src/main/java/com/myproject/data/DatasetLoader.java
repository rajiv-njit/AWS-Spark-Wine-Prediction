package com.myproject.data;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class DatasetLoader {

    public static Dataset<Row> loadTrainingDataset(SparkSession spark) {
        // Load TrainingDataset.csv from AWS S3
        String trainingDataPath = "s3://s3-inputs-model-training/TrainingDataset.csv";
        return spark.read()
                .option("header", "true")
                .csv(trainingDataPath);
    }

    public static Dataset<Row> loadValidationDataset(SparkSession spark) {
        // Load ValidationDataset.csv from AWS S3
        String validationDataPath = "s3://s3-inputs-model-training/ValidationDataset.csv";
        return spark.read()
                .option("header", "true")
                .csv(validationDataPath);
    }
}
