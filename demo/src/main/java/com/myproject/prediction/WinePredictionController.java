package com.myproject.prediction;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WinePredictionController {

    private final WinePredictionService predictionService;

    @Autowired
    public WinePredictionController(WinePredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @GetMapping("/predict")
    public String predictWine() {
        // For demonstration purposes, you can return a simple message
        return "Wine Prediction Result";
    }

    @GetMapping("/predictQuality")
    public Dataset<Row> predictWineQuality() {
        // Assuming you have a method to load new data for prediction
        Dataset<Row> newData = loadNewDataForPrediction();

        // Call the predict method from the service
        return predictionService.predictWineQuality(newData);
    }

    // This is just a placeholder method; you should replace it with your actual data loading logic
    private Dataset<Row> loadNewDataForPrediction() {
        // Implement logic to load new data for prediction
        // For example, load from a database or another data source
        return null; // Replace this with your actual implementation
    }
}


