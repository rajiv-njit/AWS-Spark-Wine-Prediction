package com.myproject.spark.training;

import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.stereotype.Component;

@Component
public class ModelTrainingModel {

    public PipelineModel train(Dataset<Row> trainingData) {
        // Assuming features are in columns "fixed acidity" to "alcohol"
        String[] featureColumns = {"fixed acidity", "volatile acidity", "citric acid", "residual sugar", "chlorides",
                "free sulfur dioxide", "total sulfur dioxide", "density", "pH", "sulphates", "alcohol"};

        // Combine feature columns into a single "features" column
        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(featureColumns)
                .setOutputCol("features");

        // Define a Linear Regression model
        LinearRegression lr = new LinearRegression()
                .setLabelCol("quality") // Set the label column
                .setFeaturesCol("features");

        // Create a pipeline with the VectorAssembler and Linear Regression
        Pipeline regressionPipeline = new Pipeline().setStages(new PipelineStage[]{assembler, lr});

        // Fit the pipeline to the training data to train the Linear Regression model
        return regressionPipeline.fit(trainingData);
    }
}
