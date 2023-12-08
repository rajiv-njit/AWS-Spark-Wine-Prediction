# Use a base image with Java
FROM openjdk:8-jdk

# Install necessary tools
RUN apt-get update && apt-get install -y bash curl tar

# Download and Install Spark
RUN curl -SL https://archive.apache.org/dist/spark/spark-3.4.1/spark-3.4.1-bin-hadoop3.tgz | tar xz -C /opt/
RUN mv /opt/spark-3.4.1-bin-hadoop3 /opt/spark
ENV SPARK_HOME=/opt/spark

# Add Spark's JARs to the classpath
ENV CLASSPATH=$CLASSPATH:$SPARK_HOME/jars/*

# Set the working directory
WORKDIR /app

# Copy the Prediction Module JAR and configuration files into the image
COPY PredictionModule/target/PredictionModule-2.0.0.jar /app/PredictionModule.jar
COPY PredictionModule/src/main/java/com/myproject/prediction/spark-config.properties /app/spark-config.properties

# Copy Datasets directory into the image
COPY Datasets /Datasets

# Define the command to run your application
ENTRYPOINT ["java", "-cp", "/app/PredictionModule.jar:/opt/spark/jars/*", "com.myproject.prediction.SparkWineQualityPredictor"]


