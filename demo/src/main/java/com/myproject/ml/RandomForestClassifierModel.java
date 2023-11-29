package com.myproject.ml;

import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.RandomForestClassifier;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomForestClassifierModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(RandomForestClassifierModel.class);

    public static PipelineModel trainModel(Dataset<Row> trainingData) {
        try {
            // Assuming the label column is "label"
            String labelCol = "label";

            // Create a vector assembler
            VectorAssembler assembler = new VectorAssembler()
                    .setInputCols(trainingData.columns())
                    .setOutputCol("features");

            Dataset<Row> assembledData = assembler.transform(trainingData);

            // Create a Random Forest Classifier model
            RandomForestClassifier randomForestClassifier = new RandomForestClassifier()
                    .setLabelCol(labelCol)
                    .setFeaturesCol("features")
                    .setNumTrees(21)
                    .setFeatureSubsetStrategy("auto")
                    .setImpurity("gini")
                    .setMaxDepth(30)
                    .setMaxBins(32);

            // Create a pipeline
            Pipeline pipeline = new Pipeline()
                    .setStages(new PipelineStage[]{
                            new StringIndexer().setInputCol("label").setOutputCol("indexedLabel"),
                            new StringIndexer().setInputCol("features").setOutputCol("indexedFeatures"),
                            randomForestClassifier
                    });

            // Train the model
            return pipeline.fit(assembledData);
        } catch (Exception e) {
            LOGGER.error("Error training Random Forest Classifier model: {}", e.getMessage(), e);
            throw new RuntimeException("Error training Random Forest Classifier model", e);
        }
    }
}
