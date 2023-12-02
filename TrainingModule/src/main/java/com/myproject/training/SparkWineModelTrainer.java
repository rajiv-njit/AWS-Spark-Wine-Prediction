package com.myproject.training;

import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;


import java.io.IOException;


public class SparkWineModelTrainer {

    public static void main(String[] args) {
        System.out.println("Model Training Application");

        SparkSession sparkSession = null;
        try {
            System.out.println("Initializing Spark:");
            sparkSession = Utility.initializeSparkSession("/home/hadoop/spark-config.properties");

            System.out.println("Training Logistic Regression Model...");

            // Initial Training
            Dataset<Row> wineDataFrame = Utility.readDataframeFromCsvFile(sparkSession, "s3://s3-inputs-model-training/TrainingDataset.csv");
            Dataset<Row> assemblyResult = Utility.assembleDataframe(wineDataFrame);

            LogisticRegression logisticRegression = new LogisticRegression()
                    .setFeaturesCol("features")
                    .setRegParam(0.2)
                    .setMaxIter(15)
                    .setLabelCol("quality");
            LogisticRegressionModel lrModel = logisticRegression.fit(assemblyResult);

            System.out.println("Validating Trained Model");

            // Validating Trained Model
            Dataset<Row> validationDataFrame = Utility.readDataframeFromCsvFile(sparkSession, "s3://s3-inputs-model-training/ValidationDataset.csv");
            Dataset<Row> assembledValidationDataFrame = Utility.assembleDataframe(validationDataFrame);
            Dataset<Row> modelTransformationResult = Utility.transformDataframeWithModel(lrModel, assembledValidationDataFrame);

            System.out.println("Validation Results");

            // Print Results
            Utility.evaluateAndSummarizeDataModel(modelTransformationResult);

            System.out.println("Saving trained model.");

            // Save new model.
            lrModel.write().overwrite().save("model");

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
