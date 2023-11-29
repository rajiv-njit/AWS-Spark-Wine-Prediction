package com.myproject.ml;

import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogisticRegressionModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogisticRegressionModel.class);

    private PipelineModel model;

    public LogisticRegressionModel trainModel(Dataset<Row> trainingData) {
        try {
            // Assuming the label column is "label"
            String labelCol = "label";

            // Create a vector assembler
            VectorAssembler assembler = new VectorAssembler()
                    .setInputCols(trainingData.columns())
                    .setOutputCol("features");

            Dataset<Row> assembledData = assembler.transform(trainingData);

            // Create a Logistic Regression model
            LogisticRegression logisticRegression = new LogisticRegression()
                    .setLabelCol(labelCol)
                    .setFeaturesCol("features")
                    .setMaxIter(100);

            // Create a pipeline
            Pipeline pipeline = new Pipeline()
                    .setStages(new PipelineStage[]{
                            new VectorAssembler().setInputCols(trainingData.columns()).setOutputCol("features"),
                            new LogisticRegression().setLabelCol(labelCol).setFeaturesCol("features").setMaxIter(100)
                    });

            // Train the model
            model = pipeline.fit(assembledData);

            return this;
        } catch (Exception e) {
            LOGGER.error("Error training Logistic Regression model: {}", e.getMessage(), e);
            throw new RuntimeException("Error training Logistic Regression model", e);
        }
    }

    public Dataset<Row> predict(Dataset<Row> data) {
        try {
            // Use the trained Logistic Regression model for prediction
            return model.transform(data);
        } catch (Exception e) {
            LOGGER.error("Error predicting with Logistic Regression: {}", e.getMessage(), e);
            throw new RuntimeException("Error predicting with Logistic Regression", e);
        }
    }
}
