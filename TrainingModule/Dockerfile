# Use a base image with Java, as your application is Java-based
FROM openjdk:8-jdk

# Install necessary tools
RUN apt-get update && apt-get install -y bash curl tar

# Download and Install Spark
RUN curl -SL https://archive.apache.org/dist/spark/spark-3.4.1/spark-3.4.1-bin-hadoop3.tgz | tar xz -C /opt/
RUN mv /opt/spark-3.4.1-bin-hadoop3 /opt/spark
ENV SPARK_HOME=/opt/spark

# Add Spark's JARs to the classpath
ENV CLASSPATH=$CLASSPATH:$SPARK_HOME/jars/*

# Copy your packaged JAR and any other necessary files into the image
COPY TrainingModule/target/TrainingModule-2.0.0.jar /app/TrainingModule.jar
COPY TrainingModule/src/main/java/com/myproject/training/spark-config.properties /home/hadoop/spark-config.properties

# Copy Datasets directory into the image
COPY Datasets /Datasets

# Define the command to run your application
ENTRYPOINT ["sh", "-c", "java -cp /app/TrainingModule.jar:/opt/spark/jars/* com.myproject.training.SparkWineModelTrainer"]
