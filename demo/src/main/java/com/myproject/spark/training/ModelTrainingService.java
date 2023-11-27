package com.myproject.spark.training;

import org.apache.spark.ml.PipelineModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModelTrainingService {

    private final ModelTrainingModel trainingModel;

    @Autowired
    public ModelTrainingService(ModelTrainingModel trainingModel) {
        this.trainingModel = trainingModel;
    }

    public void trainModel(Dataset<Row> trainingData) {
        // Assuming you have a method in ModelTrainingModel to train the model
        PipelineModel trainedModel = trainingModel.train(trainingData);

        // You can save the trained model or perform any additional tasks here
    }
}

