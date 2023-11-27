// AppConfig.java
package com.myproject.config;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.myproject.ml.*;

@Configuration
public class AppConfig {

    @Bean
    public Dataset<Row> createDataset(SparkSession spark) {
        // Your logic to create and return a Dataset<Row>
        // For example, loading data from AWS S3
        String trainingDataPath = "s3://s3-inputs-model-training/TrainingDataset.csv";
        return spark.read()
                .option("header", "true")
                .csv(trainingDataPath);
    }

    @Bean
    public LinearRegressionModel linearRegressionModel(Dataset<Row> trainingData) {
        return new LinearRegressionModel(trainingData);
    }

    @Bean
    public LogisticRegressionModel logisticRegressionModel(Dataset<Row> trainingData) {
        return new LogisticRegressionModel(trainingData);
    }

    @Bean
    public RandomForestClassifierModel randomForestClassifierModel(Dataset<Row> trainingData) {
        return new RandomForestClassifierModel(trainingData);
    }
}
