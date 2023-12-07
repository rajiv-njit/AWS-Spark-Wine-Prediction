package com.myproject.prediction;

import org.apache.spark.ml.classification.*;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import java.util.logging.Level;

public class SparkWineQualityPredictor {

    private static final Logger LOGGER = Logger.getLogger(SparkWineQualityPredictor.class.getName());

    public static void main(String[] args) {
        System.out.println("Wine Quality Prediction Application");

        if (args.length < 4) {
            System.err.println("Usage: SparkWineQualityPredictor <config file path> <test dataset path> <model directory path> <output file path>");
            System.exit(1);
        }

        String configFilePath = args[0];
        String testDataFilePath = args[1];
        String modelDirectoryPath = args[2];
        String outputFilePath = args[3] + "prediction-evaluation-results-" + new SimpleDateFormat("yyyyMMddHHmm'.txt'").format(new Date());

        LOGGER.info("Reading test data from: " + testDataFilePath);

        try (SparkSession sparkSession = Utility.initializeSparkSession(configFilePath)) {
            // Read and assemble the test data
            Dataset<Row> testData = Utility.readDataframeFromCsvFile(sparkSession, testDataFilePath);
            Dataset<Row> assembledTestData = Utility.assembleDataframe(testData);

            // Evaluate and predict using models
            StringBuilder evaluationResults = new StringBuilder();
            evaluationResults.append(evaluateModel("Logistic Regression", LogisticRegressionModel.load(modelDirectoryPath + "/logisticRegressionModel"), assembledTestData));
            evaluationResults.append(evaluateModel("Random Forest", RandomForestClassificationModel.load(modelDirectoryPath + "/randomForestModel"), assembledTestData));
            evaluationResults.append(evaluateModel("Decision Tree", DecisionTreeClassificationModel.load(modelDirectoryPath + "/decisionTreeModel"), assembledTestData));

            // Write the evaluation results to a file
            writeToFile(evaluationResults.toString(), outputFilePath);
            System.out.println("Evaluation results saved to: " + outputFilePath);

            sparkSession.stop();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing test data", e);
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String evaluateModel(String modelName, ClassificationModel<?, ?> model, Dataset<Row> assembledTestData) {
        Dataset<Row> predictions = model.transform(assembledTestData);
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setLabelCol("quality")
                .setPredictionCol("prediction")
                .setMetricName("f1");
        double f1Score = evaluator.evaluate(predictions);
        String result = modelName + " F1 Score: " + f1Score + "\n";
        LOGGER.info(result);
        return result;
    }

    private static void writeToFile(String content, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        }
    }
}
