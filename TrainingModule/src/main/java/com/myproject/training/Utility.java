package com.myproject.training;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class Utility {

    public static final String APP_NAME = "Wine-Quality-Prediction";

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
    .appName("Wine-Quality-Prediction")
    .master(masterUrl)
    .config("spark.executor.instances", executorInstances)
    .config("spark.executor.cores", executorCores)
    .config("spark.executor.memory", executorMemory)
    // Set AWS S3 access configurations for public bucket
    .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
    .config("spark.hadoop.fs.s3a.path.style.access", "true")
    .config("spark.hadoop.fs.s3a.aws.credentials.provider", "org.apache.hadoop.fs.s3a.AnonymousAWSCredentialsProvider")
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
    
        // Rename columns to remove extra quotes
        for (String column : df.columns()) {
            df = df.withColumnRenamed(column, column.replace("\"", ""));
        }
    
        return df;
    }
    

    public static Dataset<Row> assembleDataframe(Dataset<Row> dataframe) {
        VectorAssembler vectorAssembler = new VectorAssembler()
                .setInputCols(FEATURE_COLUMNS)
                .setOutputCol("features");
        Dataset<Row> assemblerResult = vectorAssembler.transform(dataframe);
        return assemblerResult.select("quality", "features");
    }

    public static void evaluateAndSummarizeDataModel(Dataset<Row> dataFrame) {
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setLabelCol("quality")
                .setPredictionCol("prediction");

        double accuracy = evaluator.setMetricName("accuracy").evaluate(dataFrame);
        double f1 = evaluator.setMetricName("f1").evaluate(dataFrame);
        System.out.println("Model Accuracy:  " + accuracy);
        System.out.println("F1 Score: " + f1);
    }

    public static Dataset<Row> transformDataframeWithModel(LogisticRegressionModel model, Dataset<Row> dataFrame) {
        return model.transform(dataFrame).select("features", "quality", "prediction");
    }
}
