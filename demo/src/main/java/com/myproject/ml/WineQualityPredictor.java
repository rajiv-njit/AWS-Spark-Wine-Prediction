package com.myproject.ml;

import org.apache.spark.ml.classification.RandomForestClassifier;
import org.apache.spark.ml.classification.RandomForestClassificationModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WineQualityPredictor {
    private static final Logger LOGGER = LoggerFactory.getLogger(WineQualityPredictor.class);

    private RandomForestClassificationModel randomForestClassifierModel;

    public void trainModel(Dataset<Row> trainingData) {
        try {
            // Assuming features are in columns "fixed acidity" to "alcohol"
            String[] featureColumns = {"fixed acidity", "volatile acidity", "citric acid", "residual sugar", "chlorides",
                    "free sulfur dioxide", "total sulfur dioxide", "density", "pH", "sulphates", "alcohol"};

            // Combine feature columns into a single "features" column
            VectorAssembler assembler = new VectorAssembler()
                    .setInputCols(featureColumns)
                    .setOutputCol("features");

            // Transform the training data to include the "features" column
            Dataset<Row> assembledData = assembler.transform(trainingData);

            // Define a RandomForestClassifier model
            RandomForestClassifier rf = new RandomForestClassifier()
                    .setLabelCol("quality") // Set the label column
                    .setFeaturesCol("features");

            // Train the RandomForestClassifier model
            randomForestClassifierModel = rf.fit(assembledData);
        } catch (Exception e) {
            LOGGER.error("Error training model: {}", e.getMessage(), e);
            throw new RuntimeException("Error training model", e);
        }
    }

    public Dataset<Row> predict(Dataset<Row> data) {
        try {
            // Assuming you have a "features" column in your data
            return randomForestClassifierModel.transform(data);
        } catch (Exception e) {
            LOGGER.error("Error predicting with RandomForestClassifier: {}", e.getMessage(), e);
            throw new RuntimeException("Error predicting with RandomForestClassifier", e);
        }
    }
}
