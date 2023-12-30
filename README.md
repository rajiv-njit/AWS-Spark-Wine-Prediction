# Overview
This project develops a Spark application for predicting the quality of wine using RandomForestClassifier and Logistic Regression models. It utilizes Apache Spark's MLlib for model training and predictions, emphasizing data processing, model accuracy, and scalability.

# Features
Model Training: Implements RandomForestClassifier and Logistic Regression models.
Data Processing: Processes datasets for training and validation, utilizing features from "fixed acidity" to "alcohol".
Modular Architecture: WineQualityPredictor class manages model workflows, ensuring readability and maintainability.
Optimized Configuration: Configures application settings for data paths, model parameters, and SparkSession optimizations.

# Local Setup and Testing

## Environment Setup:
Install Java and Apache Spark.
Clone the repository and navigate to the project directory.

## Running the Application:
For Training: java -cp /path/to/TrainingModule.jar com.myproject.training.SparkWineModelTrainer /path/to/spark-config /path/to/TrainingDataset.csv /path/to/ValidationDataset.csv
For Prediction: java -cp /path/to/PredictionModule.jar com.myproject.prediction.SparkWineQualityPredictor /path/to/spark-config /path/to/TestDataset.csv /path/to/model/directory /path/to/output

# Docker Deployment

## Building and Running with Docker:
Build: docker build -t training-module . (in the TrainingModule directory)
Run: docker run -v /local/path:/app/data training-module
Similar steps for PredictionModule.

# AWS EMR Deployment
Deploy on AWS EMR for distributed processing.
Utilizes multi-node clusters for efficient data handling.

# Repository Structure
TrainingModule/: Contains the source code and Dockerfile for the training module.
PredictionModule/: Contains the source code and Dockerfile for the prediction module.
Datasets/: Sample datasets for training, validation, and testing.
README.md: Project documentation.
