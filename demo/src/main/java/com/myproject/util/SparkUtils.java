// SparkUtils.java
package com.myproject.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import com.myproject.config.AWSConfig;

@Component
public class SparkUtils {

    @Autowired
    private AWSConfig awsConfig;
    private S3Client s3Client;

    public SparkSession getOrCreateSparkSession() {
        System.out.println("Creating Spark Session...");
        SparkSession spark = SparkSession.builder()
                .appName("my-spark-app")
                .master("local[*]")  // Set the master URL here
                .getOrCreate();

        setAWSCredentials(spark);
        setHadoopConfigurations(spark);

        System.out.println("Spark Session created successfully.");
        return spark;
    }

    public S3Client getS3Client() {
        return s3Client;
    }

    public void setS3Client(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    private void setHadoopConfigurations(SparkSession spark) {
        System.out.println("Setting Hadoop Configurations...");
        Configuration hadoopConfig = spark.sparkContext().hadoopConfiguration();
        hadoopConfig.set("fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem");
        hadoopConfig.set("fs.s3a.access.key", awsConfig.getAccessKeyId());
        hadoopConfig.set("fs.s3a.secret.key", awsConfig.getSecretKey());
        hadoopConfig.set("fs.s3a.endpoint", "s3.amazonaws.com"); // Update the endpoint if needed
        hadoopConfig.set("fs.s3a.connection.ssl.enabled", "false"); // Set to true if you are using HTTPS
        System.out.println("Hadoop Configurations set successfully.");
    }

    private void setAWSCredentials(SparkSession spark) {
        System.out.println("Setting AWS Credentials...");
        AwsCredentialsProvider awsCredentialsProvider = awsConfig.awsCredentialsProvider();
        S3Client s3Client = awsConfig.s3Client(awsCredentialsProvider);
        
        // You can also set the AWS credentials directly in SparkSession
        spark.conf().set("spark.hadoop.fs.s3a.access.key", awsConfig.getAccessKeyId());
        spark.conf().set("spark.hadoop.fs.s3a.secret.key", awsConfig.getSecretKey());
        spark.conf().set("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem");
        spark.conf().set("spark.hadoop.fs.s3a.multiobjectdelete.enable", "false");
        spark.conf().set("spark.hadoop.fs.s3a.connection.maximum", "1000");  // Updated line
        System.out.println("AWS Credentials set successfully.");
    }
}
