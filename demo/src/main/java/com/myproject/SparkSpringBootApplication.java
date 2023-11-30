package com.myproject;

import com.myproject.config.AppConfig;
import com.myproject.config.SparkConfig;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.io.File;


import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.classification.LogisticRegressionTrainingSummary;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import scala.Tuple2;

@SpringBootApplication
@ComponentScan(basePackageClasses = { SparkSpringBootApplication.class, AppConfig.class, SparkConfig.class })
public class SparkSpringBootApplication implements CommandLineRunner {

    @Autowired
    private JavaSparkContext javaSparkContext;

    static String TRAINING_DATA_FILE_NAME = "Dataset/TrainingDataset.csv";
    static String VALIDATION_DATA_FILE_NAME = "Dataset/ValidationDataset.csv";
    static final long SPLIT_SEED = 123L;
    static final String COLUMN_SCORE_1 = "score1";
    static final String COLUMN_SCORE_2 = "score2";
    static final String COLUMN_PREDICTION = "result";
    static final String COLUMN_INPUT_FEATURES = "inputFeatures";
    private long truePositives;
    private long falsePositives;
    private long falseNegatives;
    private double precision;
    private double recall;
    private double fScore;

    public static void main(String[] args) {
        SpringApplication.run(SparkSpringBootApplication.class, args);
    }

    @Override
    public void run(String... args) {
        SparkSession sparkSession = SparkSession.builder().sparkContext(javaSparkContext.sc()).getOrCreate();

        String trainingDataFilePath = "src/main/java/com/myproject/" + TRAINING_DATA_FILE_NAME;
        String validationDataFilePath = "src/main/java/com/myproject/" + VALIDATION_DATA_FILE_NAME;

        // Load training data with the specified delimiter and infer the schema
        Dataset<Row> trainingDataSet = sparkSession.read().option("header", "true").option("delimiter", ";")
                .option("inferSchema", "true") // Automatically infer the schema
                .csv(trainingDataFilePath);

        // Manually remove extra quotes from the header
        trainingDataSet = fixCsvHeader(trainingDataSet);

        // Load validation data with the specified delimiter and infer the schema
        Dataset<Row> validationDataSet = sparkSession.read().option("header", "true").option("delimiter", ";")
                .option("inferSchema", "true") // Automatically infer the schema
                .csv(validationDataFilePath);

        // Manually remove extra quotes from the header
        validationDataSet = fixCsvHeader(validationDataSet);

        // Log the number of records in training and validation datasets
        long trainingRecords = trainingDataSet.count();
        long validationRecords = validationDataSet.count();
        System.out.println("Number of records in Training Dataset: " + trainingRecords);
        System.out.println("Number of records in Validation Dataset: " + validationRecords);

        // Feature engineering: Create inputFeatures column
        org.apache.spark.ml.feature.VectorAssembler vectorAssembler = new org.apache.spark.ml.feature.VectorAssembler()
                .setInputCols(new String[] { "fixed acidity", "volatile acidity", "citric acid", "residual sugar",
                        "chlorides", "free sulfur dioxide", "total sulfur dioxide", "density", "pH", "sulphates",
                        "alcohol" })
                .setOutputCol(COLUMN_INPUT_FEATURES);

        trainingDataSet = vectorAssembler.transform(trainingDataSet);
        validationDataSet = vectorAssembler.transform(validationDataSet);

        LogisticRegression logisticRegression = new LogisticRegression().setMaxIter(100).setRegParam(0.3)
                .setElasticNetParam(0.8);

        logisticRegression.setLabelCol("quality"); // Assuming "quality" is the column to predict
        logisticRegression.setFeaturesCol(COLUMN_INPUT_FEATURES);

        // Train the model
        LogisticRegressionModel logisticRegressionModel = logisticRegression.fit(trainingDataSet);
        LogisticRegressionTrainingSummary logisticRegressionTrainingSummary = logisticRegressionModel.summary();

        // Evaluate on the validation set
        Dataset<Row> validationDataSetPredictions = logisticRegressionModel.transform(validationDataSet);
        JavaPairRDD<Double, Double> validationPredictionRDD = convertToJavaRDDPair(validationDataSetPredictions);
        AppConfig.printFScoreBinaryClassfication(validationPredictionRDD);

        // Log results and summary
        System.out.println("Summary of Logistic Regression Model Training:");
        System.out.println("Number of iterations: " + logisticRegressionTrainingSummary.totalIterations());
        System.out.println("Objective history: " + Arrays.toString(logisticRegressionTrainingSummary.objectiveHistory()));

        // Log F-score for binary classification
        System.out.println("F-score for Binary Classification:");
        AppConfig.printFScoreBinaryClassfication(validationPredictionRDD);

        // Log results in prediction.html
        logResultsToHtml(validationPredictionRDD, trainingRecords, validationRecords);

        sparkSession.stop();
    }

    private JavaPairRDD<Double, Double> convertToJavaRDDPair(Dataset<Row> rowsData) {
        JavaRDD<Row> rowsRdd = rowsData.toJavaRDD();
        return rowsRdd.mapToPair(row -> new Tuple2<>(row.getDouble(2), row.getDouble(0))); // Assuming "quality" is at
                                                                                             // index 2
    }

    private Dataset<Row> fixCsvHeader(Dataset<Row> dataSet) {
        String[] columns = dataSet.columns();
        for (int i = 0; i < columns.length; i++) {
            columns[i] = columns[i].replaceAll("\"", "");
        }
        return dataSet.toDF(columns);
    }

    private void logResultsToHtml(JavaPairRDD<Double, Double> validationPredictionRDD, long trainingRecords,
            long validationRecords) {
        // Prepare HTML content
        StringBuilder htmlContent = new StringBuilder("<html><body>");
        htmlContent.append("<h2>Spark Model Prediction Results Summary</h2>");
        htmlContent.append("<p>Number of records in Training Dataset: ").append(trainingRecords).append("</p>");
        htmlContent.append("<p>Number of records in Validation Dataset: ").append(validationRecords).append("</p>");

        // Log F-score for binary classification
        htmlContent.append("<h3>F-score for Binary Classification:</h3>");
        htmlContent.append("<pre>");
        htmlContent.append("True positives: ").append(truePositives).append("\n");
        htmlContent.append("False positives: ").append(falsePositives).append("\n");
        htmlContent.append("False negatives: ").append(falseNegatives).append("\n");
        htmlContent.append("Precision: ").append(precision).append("\n");
        htmlContent.append("Recall: ").append(recall).append("\n");
        htmlContent.append("FScore: ").append(fScore).append("\n");
        htmlContent.append("</pre>");

        // Include additional details or logs as needed

        htmlContent.append("</body></html>");

        // Write HTML content to the specified file
        String htmlFilePath = AppConfig.getResourcesDirectoryPath() + "templates/prediction.html";
        try (FileWriter writer = new FileWriter(htmlFilePath)) {
            writer.write(htmlContent.toString());
            System.out.println("Results written to prediction.html");
        } catch (IOException e) {
            System.err.println("Error writing results to prediction.html: " + e.getMessage());
        }
    }

}
