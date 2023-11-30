package com.myproject.winequalityprediction;

import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.classification.LogisticRegressionTrainingSummary;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@SpringBootApplication
public class App {

    @Autowired
    private AppSession appSession;

    private Dataset<Row> result;
    private Dataset<Row> validate;
    private Dataset<Row> wineData;
    private Dataset<Row> validationData;
    private VectorAssembler vectorAssembler;
    private LogisticRegressionTrainingSummary logisticRegressionTrainingSummary;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @PostConstruct
    public void initialize() {
        connect();
        regression();
        result();
    }

    public void connect() {
        appSession.setSparkSession("spark://192.168.1.222:7070", "192.168.1.222"); // Set your desired bind address
    }
    
    public void regression() {
        LogisticRegression logisticRegression = new LogisticRegression()
                .setFeaturesCol("features")
                .setRegParam(0.2)
                .setMaxIter(15)
                .setLabelCol("quality");

        wineData = appSession.getSparkSession().read()
                .option("inferSchema", true)
                .option("header", true)
                .option("delimiter", ";")
                .csv("TrainingDataset.csv");

        validationData = appSession.getSparkSession().read()
                .option("inferSchema", true)
                .option("header", true)
                .option("delimiter", ";")
                .csv("ValidationDataset.csv");

        vectorAssembler = new VectorAssembler()
                .setInputCols(new String[]{"fixed acidity", "volatile acidity", "citric acid", "residual sugar", "chlorides", "free sulfur dioxide", "total sulfur dioxide", "density", "pH", "sulphates", "alcohol"})
                .setOutputCol("features");

        result = vectorAssembler.transform(wineData).select("quality", "features");
        result.show();

        LogisticRegressionModel logModel = logisticRegression.fit(result);

        logModel.setThreshold(0.2);
        validate = vectorAssembler.transform(validationData).select("quality", "features");

        Dataset<Row> winePrediction = logModel.transform(validate).select("features", "quality", "prediction");

        winePrediction.show();

        LogisticRegressionModel logisticRegressionModel = logisticRegression.fit(result);
        logisticRegressionTrainingSummary = logisticRegressionModel.summary();
    }

    public void result() {
        System.out.println("F Score:" + logisticRegressionTrainingSummary.labelCol().toString());
        System.out.print(Arrays.toString(logisticRegressionTrainingSummary.fMeasureByLabel()) + "\n");
    }
}
