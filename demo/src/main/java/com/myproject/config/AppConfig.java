package com.myproject.config;

import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.util.StringUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.services.s3.S3Client;

@org.springframework.context.annotation.Configuration
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = "com.myproject.config")
public class AppConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

    @Value("${spark.app.name}")
    private String appName;

    @Value("${spark.master}")
    private String master;

    @Value("${aws.region}")
    private String region;

    @Value("${aws_access_key_id:#{null}}")
    private String accessKeyId;

    @Value("${aws_secret_access_key:#{null}}")
    private String secretKey;

    private S3Client s3Client;

    @Primary
    @Bean
    public SparkSession sparkSession() {
        try {
            LOGGER.info("Creating Spark Session...");

            SparkConf sparkConf = new SparkConf()
                    .setAppName(appName)
                    .setMaster(master)
                    .set("spark.driver.bindAddress", "127.0.0.1")
                    .set("spark.driver.port", "7077");

            LOGGER.info("SparkConf created.");

            SparkSession spark = SparkSession.builder()
                    .config(sparkConf)
                    .getOrCreate();

            LOGGER.info("Spark Session created successfully.");

            setAWSCredentials(spark);
            setHadoopConfigurations(spark);

            return spark;
        } catch (Exception e) {
            LOGGER.error("Error creating Spark Session: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating Spark Session", e);
        }
    }

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        if (StringUtils.hasText(accessKeyId) && StringUtils.hasText(secretKey)) {
            LOGGER.info("Using provided access key and secret key");
            return () -> AwsBasicCredentials.create(accessKeyId, secretKey);
        } else {
            LOGGER.info("Attempting to load credentials from providers chain");
            AwsCredentialsProviderChain credentialsProviderChain = AwsCredentialsProviderChain.builder()
                    .credentialsProviders(
                            SystemPropertyCredentialsProvider.create(),
                            EnvironmentVariableCredentialsProvider.create()
                    )
                    .build();

            try {
                LOGGER.info("Loaded access key: {}", credentialsProviderChain.resolveCredentials().accessKeyId());
            } catch (Exception e) {
                LOGGER.error("Failed to load access key from providers chain: {}", e.getMessage(), e);
            }

            return credentialsProviderChain;
        }
    }

    @Bean
    public AmazonS3 amazonS3Client(AWSStaticCredentialsProvider awsCredentialsProvider) {
        AWSCredentials awsCredentials = awsCredentialsProvider.getCredentials();

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(awsCredentialsProvider)
                .withRegion(region)
                .build();

        // Print AWS credentials for debugging
        LOGGER.info("Access Key ID: {}", awsCredentials.getAWSAccessKeyId());
        LOGGER.info("Secret Access Key: {}", awsCredentials.getAWSSecretKey());

        LOGGER.info("AmazonS3 client created successfully.");

        return s3Client;
    }

    // Add this method to set S3Client
    public void setS3Client(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Bean
    public Dataset<Row> createDataset(SparkSession spark) {
        String trainingDataPath = "s3://s3-inputs-model-training/TrainingDataset.csv";
        return spark.read()
                .option("header", "true")
                .csv(trainingDataPath);
    }

    private void setHadoopConfigurations(SparkSession spark) {
        LOGGER.info("Setting Hadoop Configurations...");
        Configuration hadoopConfig = spark.sparkContext().hadoopConfiguration();

        // Set the Hadoop native library directory
        String hadoopNativeLibDir = "/Users/rajivkumar/Desktop/hadoop-3.3.4/lib/native";
        System.setProperty("hadoop.native.lib.dir", hadoopNativeLibDir);

        // Log the path
        LOGGER.info("Hadoop native library directory: {}", hadoopNativeLibDir);

        hadoopConfig.set("fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem");

        // Retrieve AWS credentials from environment variables
        AwsCredentialsProvider awsCredentialsProvider = awsCredentialsProvider();

        // Set AWS access key and secret key from the credentials provider
        hadoopConfig.set("fs.s3a.access.key", awsCredentialsProvider.resolveCredentials().accessKeyId());
        hadoopConfig.set("fs.s3a.secret.key", awsCredentialsProvider.resolveCredentials().secretAccessKey());

        hadoopConfig.set("fs.s3a.multiobjectdelete.enable", "false");
        hadoopConfig.set("fs.s3a.connection.maximum", "1000");
        LOGGER.info("Hadoop Configurations set successfully.");
    }

    private void setAWSCredentials(SparkSession spark) {
        LOGGER.info("Setting AWS Credentials...");

        try {
            AwsCredentialsProvider awsCredentialsProvider = awsCredentialsProvider();
            // Print AWS credentials for debugging
            LOGGER.info("Access Key ID: {}", awsCredentialsProvider.resolveCredentials().accessKeyId());
            LOGGER.info("Secret Access Key: {}", awsCredentialsProvider.resolveCredentials().secretAccessKey());
            spark.conf().set("spark.hadoop.fs.s3a.access.key", awsCredentialsProvider.resolveCredentials().accessKeyId());
            spark.conf().set("spark.hadoop.fs.s3a.secret.key", awsCredentialsProvider.resolveCredentials().secretAccessKey());
            spark.conf().set("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem");
            spark.conf().set("spark.hadoop.fs.s3a.multiobjectdelete.enable", "false");
            spark.conf().set("spark.hadoop.fs.s3a.connection.maximum", "1000");
            LOGGER.info("AWS Credentials set successfully.");
        } catch (Exception e) {
            System.err.println("Failed to load AWS credentials: " + e.getMessage());
            System.err.println("Exiting application...");
            System.exit(1);
        }
    }
}
