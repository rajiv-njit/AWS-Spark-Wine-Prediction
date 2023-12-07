package com.myproject.training;

import org.apache.spark.ml.classification.*;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class SparkWineModelTrainer {

    public static void main(String[] args) {
        System.out.println("Model Training Application");

        if (args.length < 3) {
            System.err.println("Usage: SparkWineModelTrainer <config file path> <training dataset path> <validation dataset path>");
            System.exit(1);
        }

        String configFilePath = args[0];
        String trainingDataPath = args[1];
        String validationDataPath = args[2];

        StringBuilder htmlResults = new StringBuilder();
        SparkSession sparkSession = null;
        try {
            System.out.println("Initializing Spark:");
            sparkSession = Utility.initializeSparkSession(configFilePath);

            // Read Training and Validation Data
            Dataset<Row> wineDataFrame = Utility.readDataframeFromCsvFile(sparkSession, trainingDataPath);
            Dataset<Row> assemblyResult = Utility.assembleDataframe(wineDataFrame);
            Dataset<Row> validationDataFrame = Utility.readDataframeFromCsvFile(sparkSession, validationDataPath);
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

            // Save and Log Logistic Regression Model
            String lrModelPath = "models/logisticRegressionModel";
            lrModel.write().overwrite().save(lrModelPath);
            System.out.println("Logistic Regression Model saved to: " + lrModelPath);

            // Save and Log Random Forest Model
            String rfModelPath = "models/randomForestModel";
            rfModel.write().overwrite().save(rfModelPath);
            System.out.println("Random Forest Model saved to: " + rfModelPath);

            // Save and Log Decision Tree Model
            String dtModelPath = "models/decisionTreeModel";
            dtModel.write().overwrite().save(dtModelPath);
            System.out.println("Decision Tree Model saved to: " + dtModelPath);

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
