package com.myproject.prediction;

import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Utility {

    public static final String APP_NAME = "Wine-Quality-Prediction";
    public static final String HTML_OUTPUT_PATH = "prediction-results.html";

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

        return SparkSession.builder()
                .appName(APP_NAME)
                .master(masterUrl)
                .config("spark.executor.instances", executorInstances)
                .config("spark.executor.cores", executorCores)
                .config("spark.executor.memory", executorMemory)
                .getOrCreate();
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
}
