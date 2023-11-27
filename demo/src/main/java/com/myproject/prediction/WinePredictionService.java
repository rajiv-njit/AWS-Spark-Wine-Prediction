package com.myproject.prediction;

import org.apache.spark.ml.PipelineModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WinePredictionService {

    private final WinePredictionModel predictionModel;

    @Autowired
    public WinePredictionService(PipelineModel pipelineModel) {
        // Assuming you have a bean of type PipelineModel configured in your application context
        this.predictionModel = new WinePredictionModel(pipelineModel);
    }

    public Dataset<Row> predictWineQuality(Dataset<Row> inputData) {
        // Call the predict method of the WinePredictionModel
        return predictionModel.predict(inputData);
    }

    // You can add more methods or functionality as needed for your service
}



