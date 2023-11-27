package com.myproject.ml;
import org.apache.spark.ml.classification.RandomForestClassifier;
import org.apache.spark.ml.classification.RandomForestClassificationModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.stereotype.Component;

@Component
public class RandomForestClassifierModel {

    private final RandomForestClassificationModel model;

    // Constructor without Dataset injection
    public RandomForestClassifierModel() {
        this.model = null;  // You might initialize it differently based on your logic
    }

    // Constructor with Dataset injection
    public RandomForestClassifierModel(Dataset<Row> trainingData) {
        this.model = trainRandomForestClassifierModel(trainingData);
    }

    public Dataset<Row> predict(Dataset<Row> data) {
        if (model == null) {
            throw new IllegalStateException("Model is not trained. Please provide training data first.");
        }
        return model.transform(data);
    }

    private RandomForestClassificationModel trainRandomForestClassifierModel(Dataset<Row> trainingData) {
        RandomForestClassifier rf = new RandomForestClassifier()
                .setLabelCol("quality")
                .setFeaturesCol("features")
                .setNumTrees(10);

        return rf.fit(trainingData);
    }

    // Getter for the model
    public RandomForestClassificationModel getModel() {
        return model;
    }
}
