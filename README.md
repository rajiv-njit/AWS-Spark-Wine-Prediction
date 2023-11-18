# AWS-Spark-Wine-Prediction
Master AWS, Spark, and Docker for wine quality prediction. Develop parallel ML, train on EC2, deploy in Docker. 

# Wine Quality Prediction

This Java project, **Wine Quality Prediction**, is designed for predicting the quality of wines using machine learning. The project includes components for model training with Apache Spark, a prediction application, and a Docker container for deployment.

## Project Structure

The project follows a standard Maven structure and is organized as follows:

```plaintext
.
├── Dockerfile
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── myproject
    │   │           ├── Application.java
    │   │           ├── config
    │   │           │   └── AppConfig.java
    │   │           ├── prediction
    │   │           │   ├── controller
    │   │           │   │   └── WinePredictionController.java
    │   │           │   ├── model
    │   │           │   │   └── WinePredictionModel.java
    │   │           │   └── service
    │   │           │       └── WinePredictionService.java
    │   │           └── spark
    │   │               └── training
    │   │                   ├── controller
    │   │                   │   └── ModelTrainingController.java
    │   │                   ├── model
    │   │                   │   └── ModelTrainingModel.java
    │   │                   └── service
    │   │                       └── ModelTrainingService.java
    │   └── resources
    │       ├── application.properties
    │       ├── log4j2.xml
    │       └── templates
    │           └── prediction.html
    └── test
        ├── java
        │   └── com
        │       └── myproject
        │           └── WinePredictionControllerTest.java
        └── resources

Getting Started
Prerequisites
Java (version 8 or higher)
Apache Maven
Apache Spark
Docker (if deploying the application using Docker)
Building the Project
bash
Copy code
mvn clean install
Running the Application
To run the application locally:

# Assuming the main class is com.myproject.Application
java -cp target/my-java-project-1.0-SNAPSHOT.jar com.myproject.Application

# Docker Deployment
To build and run the Docker container:

docker build -t wine-prediction-app .
docker run -p 8080:8080 wine-prediction-app

# Testing
The project includes unit tests. Run them using:

mvn test

# Contributing
If you'd like to contribute to this project, please fork the repository and submit a pull request.