package com.myproject.prediction;

import java.io.IOException;

import org.apache.spark.ml.PipelineModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class SparkWineQualityPredictor {

    public static void main(String[] args) {
        System.out.println("Wine Quality Prediction Application");

        if (args.length < 1) {
            System.err.println("Usage: SparkWineQualityPredictor <test data file path>");
            System.exit(1);
        }

        String testDataFilePath = args[0];

        try (SparkSession sparkSession = Utility.initializeSparkSession("spark-config.properties")) {
            // Load the trained model
            PipelineModel model = PipelineModel.load("/app/data/model");

            // Read the test data
            Dataset<Row> testData = Utility.readDataframeFromCsvFile(sparkSession, testDataFilePath);

            // Assemble the test data
            Dataset<Row> assembledTestData = Utility.assembleDataframe(testData);

            // Make predictions
            Dataset<Row> predictions = model.transform(assembledTestData);

            // Show predictions
            predictions.show();

            sparkSession.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}