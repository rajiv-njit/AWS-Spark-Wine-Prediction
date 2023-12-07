package com.myproject.training;

import org.apache.spark.ml.PredictionModel;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import java.io.File;

public class Utility {
    public static final String APP_NAME = "Wine-Quality-Prediction";
    public static final String HTML_OUTPUT_PATH = "model-evaluation-results.html";

    // Update feature column names to match CSV file format
    public static final String[] FEATURE_COLUMNS = {
        "fixed acidity", "volatile acidity", "citric acid", "residual sugar",
        "chlorides", "free sulfur dioxide", "total sulfur dioxide", 
        "density", "pH", "sulphates", "alcohol"
    };

    public static SparkSession initializeSparkSession(String propertiesFilePath) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream propFile = new FileInputStream(propertiesFilePath)) {
            properties.load(propFile);
        }

        String masterUrl = properties.getProperty("spark.master.url", "local[*]");
        String executorInstances = properties.getProperty("spark.executor.instances", "1");
        String executorCores = properties.getProperty("spark.executor.cores", "1");
        String executorMemory = properties.getProperty("spark.executor.memory", "1g");

        SparkSession session = SparkSession.builder()
                .appName(APP_NAME)
                .master(masterUrl)
                .config("spark.executor.instances", executorInstances)
                .config("spark.executor.cores", executorCores)
                .config("spark.executor.memory", executorMemory)
                .getOrCreate();

        session.sparkContext().setLogLevel("OFF");
        return session;
    }

    public static Dataset<Row> readDataframeFromCsvFile(SparkSession sparkSession, String path) {
        Dataset<Row> df = sparkSession.read()
                .option("header", true)
                .option("delimiter", ";")
                .option("inferSchema", true)
                .csv(path);

        // Sanitize column names
        StructType schema = df.schema();
        for (StructField field : schema.fields()) {
            String newName = field.name().replaceAll("\"", "");
            df = df.withColumnRenamed(field.name(), newName);
        }

        return df;
    }

    public static Dataset<Row> assembleDataframe(Dataset<Row> dataframe) {
        VectorAssembler vectorAssembler = new VectorAssembler()
                .setInputCols(FEATURE_COLUMNS)
                .setOutputCol("features");
        return vectorAssembler.transform(dataframe).select("quality", "features");
    }

    public static String evaluateAndSummarizeDataModel(Dataset<Row> predictions, String modelName) {
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setLabelCol("quality")
                .setPredictionCol("prediction");
    
        double accuracy = evaluator.setMetricName("accuracy").evaluate(predictions);
        double f1Score = evaluator.setMetricName("f1").evaluate(predictions);
    
        System.out.println("Evaluation Results for " + modelName + ":");
        System.out.println("Accuracy = " + accuracy);
        System.out.println("F1 Score = " + f1Score);
    
        return "<h3>" + modelName + " Evaluation Results:</h3>"
             + "<p>Accuracy = " + accuracy + "<br>F1 Score = " + f1Score + "</p>";
    }
    

    public static void appendResultsToHtmlFile(String modelName, double accuracy, double f1Score) throws IOException {
        String htmlContent = "<h3>" + modelName + " Evaluation Results:</h3>"
                           + "<p>Accuracy = " + accuracy + "<br>F1 Score = " + f1Score + "</p>";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HTML_OUTPUT_PATH, true))) {
            writer.write(htmlContent);
        }
    }

    public static void writeResultsToHtmlFile(String htmlContent) throws IOException {
        File outputFile = new File(HTML_OUTPUT_PATH);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {
            writer.write(htmlContent);
        }
        System.out.println("Output file written to: " + outputFile.getAbsolutePath());
    } 

    public static Dataset<Row> transformDataframeWithModel(PredictionModel<?, ?> model, Dataset<Row> dataFrame) {
        return model.transform(dataFrame).select("features", "quality", "prediction");
    }
}
