package com.myproject.ml;

import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.classification.RandomForestClassifier;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WineQualityPredictor {
    private static final Logger LOGGER = LoggerFactory.getLogger(WineQualityPredictor.class);

    private PipelineModel regressionModel;
    private final RandomForestClassifierModel randomForestClassifierModel;
    private final LinearRegressionModel linearRegressionModel;
    private final LogisticRegressionModel logisticRegressionModel;

    public WineQualityPredictor(
            RandomForestClassifierModel randomForestClassifierModel,
            LinearRegressionModel linearRegressionModel,
            LogisticRegressionModel logisticRegressionModel) {
        // Initialize the models using constructor injection
        this.randomForestClassifierModel = randomForestClassifierModel;
        this.linearRegressionModel = linearRegressionModel;
        this.logisticRegressionModel = logisticRegressionModel;
    }

    public void trainModels(Dataset<Row> trainingData) {
        try {
            // Assuming features are in columns "fixed acidity" to "alcohol"
            String[] featureColumns = {"fixed acidity", "volatile acidity", "citric acid", "residual sugar", "chlorides",
                    "free sulfur dioxide", "total sulfur dioxide", "density", "pH", "sulphates", "alcohol"};

            // Combine feature columns into a single "features" column
            VectorAssembler assembler = new VectorAssembler()
                    .setInputCols(featureColumns)
                    .setOutputCol("features");

            // Define a RandomForestClassifier model
            RandomForestClassifier rf = new RandomForestClassifier()
                    .setLabelCol("quality") // Set the label column
                    .setFeaturesCol("features");

            // Create a pipeline with the VectorAssembler and RandomForestClassifier
            Pipeline regressionPipeline = new Pipeline().setStages(new PipelineStage[]{assembler, rf});

            // Fit the entire pipeline to the training data to train the RandomForestClassifier model
            regressionModel = regressionPipeline.fit(trainingData);

            // Train the Linear Regression model
            linearRegressionModel.trainModel(trainingData);

            // Train the Logistic Regression model
            logisticRegressionModel.trainModel(trainingData);
        } catch (Exception e) {
            LOGGER.error("Error training models: {}", e.getMessage(), e);
            throw new RuntimeException("Error training models", e);
        }
    }

    public Dataset<Row> predictRegression(Dataset<Row> data) {
        try {
            // Use the trained RandomForestClassifier model for prediction
            return regressionModel.transform(data);
        } catch (Exception e) {
            LOGGER.error("Error predicting with RandomForestClassifier: {}", e.getMessage(), e);
            throw new RuntimeException("Error predicting with RandomForestClassifier", e);
        }
    }

    public Dataset<Row> predictLinearRegression(Dataset<Row> data) {
        try {
            // Use the trained Linear Regression model for prediction
            return linearRegressionModel.predict(data);
        } catch (Exception e) {
            LOGGER.error("Error predicting with Linear Regression: {}", e.getMessage(), e);
            throw new RuntimeException("Error predicting with Linear Regression", e);
        }
    }

    public Dataset<Row> predictLogisticRegression(Dataset<Row> data) {
        try {
            // Use the trained Logistic Regression model for prediction
            return logisticRegressionModel.predict(data);
        } catch (Exception e) {
            LOGGER.error("Error predicting with Logistic Regression: {}", e.getMessage(), e);
            throw new RuntimeException("Error predicting with Logistic Regression", e);
        }
    }
}
