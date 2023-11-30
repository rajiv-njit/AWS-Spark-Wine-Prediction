package com.myproject.prediction;

import org.apache.spark.ml.PipelineModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.myproject.ml.RandomForestClassifierModel;

@Component
public class WinePrediction {

    private static final Logger LOGGER = LoggerFactory.getLogger(WinePrediction.class);

    private final RandomForestClassifierModel randomForestClassifierModel;

    @Autowired
    public WinePrediction(RandomForestClassifierModel randomForestClassifierModel) {
        this.randomForestClassifierModel = randomForestClassifierModel;
        // Additional initialization logic if needed
        // You can directly use randomForestClassifierModel here without reassigning
    }

    public void trainModel(Dataset<Row> trainingData) {
        try {
            // Get the trained model from RandomForestClassifierModel
            PipelineModel model = randomForestClassifierModel.trainModel(trainingData);

            // Additional logic if needed
        } catch (Exception e) {
            LOGGER.error("Error training the model: {}", e.getMessage(), e);
            throw new RuntimeException("Error training the model", e);
        }
    }

    // Other methods for prediction, evaluation, etc.
}
