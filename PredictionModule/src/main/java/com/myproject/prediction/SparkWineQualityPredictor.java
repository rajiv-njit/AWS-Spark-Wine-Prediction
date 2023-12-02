package com.myproject.prediction;

import java.io.IOException;

import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;


public class SparkWineQualityPredictor {

    public static void main(String[] args) {
        System.out.println("Initializing Spark:");

        SparkSession sparkSession = null;
        try {
            sparkSession = Utility.initializeSparkSession("/home/hadoop/spark-config.properties");

            System.out.println("Loading Test Data Set");

            // Load Test Dataframe
            Dataset<Row> testingDataFrame = Utility.readDataframeFromCsvFile(sparkSession, "../Datasets/TestDataset.csv");
            Dataset<Row> assembledTestDataFrame = Utility.assembleDataframe(testingDataFrame);

            System.out.println("Loading Training Model");

            // Load Training Model
            LogisticRegressionModel lrModel = LogisticRegressionModel.load("model");

            System.out.println("Predicting using Trained Model and Test Data");

            // Predict using Test Dataframe
            Dataset<Row> predictionData = Utility.transformDataframeWithModel(lrModel, assembledTestDataFrame);

            predictionData.show();

            System.out.println("Evaluation Results:");

            Utility.evaluateAndSummarizeDataModel(predictionData);

        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        } finally {
            if (sparkSession != null) {
                sparkSession.stop();
            }
        }
    }
}
