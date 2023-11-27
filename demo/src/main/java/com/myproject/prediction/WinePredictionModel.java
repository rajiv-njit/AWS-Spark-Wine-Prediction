package com.myproject.prediction;

import org.apache.spark.ml.PipelineModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class WinePredictionModel {

    private final PipelineModel model;

    public WinePredictionModel(PipelineModel model) {
        this.model = model;
    }

    public Dataset<Row> predict(Dataset<Row> data) {
        // Assuming you have defined the stages in your ML pipeline for preprocessing
        // and feature vectorization before the final prediction stage

        // If your final stage in the pipeline is a regression model, you can directly use it for prediction
        Dataset<Row> predictions = model.transform(data);

        // You might want to select only relevant columns from the prediction results
        // For example, if prediction column is named "prediction":
        // predictions = predictions.select("features", "prediction");

        return predictions;
    }
}
