package com.myproject.training;

import org.apache.spark.ml.classification.*;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class SparkWineModelTrainer {

    public static void main(String[] args) {
        System.out.println("Model Training Application");

        StringBuilder htmlResults = new StringBuilder();
        SparkSession sparkSession = null;
        try {
            System.out.println("Initializing Spark:");
            sparkSession = Utility.initializeSparkSession("/home/hadoop/spark-config.properties");

            // Read Training and Validation Data
            Dataset<Row> wineDataFrame = Utility.readDataframeFromCsvFile(sparkSession, "Datasets/TrainingDataset.csv");
            Dataset<Row> assemblyResult = Utility.assembleDataframe(wineDataFrame);
            Dataset<Row> validationDataFrame = Utility.readDataframeFromCsvFile(sparkSession, "Datasets/ValidationDataset.csv");
            Dataset<Row> assembledValidationDataFrame = Utility.assembleDataframe(validationDataFrame);

            // Train Logistic Regression Model
            System.out.println("Training Logistic Regression Model...");
            LogisticRegression logisticRegression = new LogisticRegression()
                    .setFeaturesCol("features")
                    .setRegParam(0.2)
                    .setMaxIter(15)
                    .setLabelCol("quality");
            LogisticRegressionModel lrModel = logisticRegression.fit(assemblyResult);
            htmlResults.append(Utility.evaluateAndSummarizeDataModel(lrModel.transform(assembledValidationDataFrame), "Logistic Regression"));

            // Train Random Forest Model
            System.out.println("Training Random Forest Model...");
            RandomForestClassifier rfClassifier = new RandomForestClassifier()
                    .setFeaturesCol("features")
                    .setLabelCol("quality");
            RandomForestClassificationModel rfModel = rfClassifier.fit(assemblyResult);
            htmlResults.append(Utility.evaluateAndSummarizeDataModel(rfModel.transform(assembledValidationDataFrame), "Random Forest"));

            // Train Decision Tree Model
            System.out.println("Training Decision Tree Model...");
            DecisionTreeClassifier dtClassifier = new DecisionTreeClassifier()
                    .setFeaturesCol("features")
                    .setLabelCol("quality");
            DecisionTreeClassificationModel dtModel = dtClassifier.fit(assemblyResult);
            htmlResults.append(Utility.evaluateAndSummarizeDataModel(dtModel.transform(assembledValidationDataFrame), "Decision Tree"));


            // Write accumulated results to HTML file
            Utility.writeResultsToHtmlFile(htmlResults.toString());

        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception appropriately
        } finally {
            if (sparkSession != null) {
                sparkSession.stop();
            }
        }
    }
}
