package com.myproject.spark.training;

import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/model-training")
public class ModelTraining {

    private final ModelTraining trainingModel;  // Renamed the field

    @Autowired
    public ModelTraining(ModelTraining trainingModel) {
        this.trainingModel = trainingModel;
    }

    @PostMapping("/start")
    @ResponseBody
    public String startTraining() {
        // Assuming you have a method to load training data
        Dataset<Row> trainingData = loadTrainingData();

        // Start the training process
        trainModel(trainingData);

        return "Training started successfully";
    }

    // This is just a placeholder method; you should replace it with your actual data loading logic
    private Dataset<Row> loadTrainingData() {
        // Implement logic to load training data
        // For example, load from a database or another data source
        return null; // Replace this with your actual implementation
    }

    private void trainModel(Dataset<Row> trainingData) {
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
        PipelineModel trainedModel = regressionPipeline.fit(trainingData);

        // You can save the trained model or perform any additional tasks here
    }
}
