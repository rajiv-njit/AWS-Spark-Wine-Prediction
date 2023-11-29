package com.myproject.config;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.regions.Region;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AwsConfig.class);

    @Value("${aws.region}")
    private String region;

    @Value("${aws_access_key_id:#{null}}")
    private String accessKeyId;

    @Value("${aws_secret_access_key:#{null}}")
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        S3Client s3Client;

        if (accessKeyId != null && secretKey != null) {
            // Use explicit credentials if available
            AwsCredentials awsCredentials = AwsBasicCredentials.create(accessKeyId, secretKey);
            AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(awsCredentials);

            s3Client = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(credentialsProvider)
                    .build();
        } else {
            // Use default credentials provider
            s3Client = S3Client.builder()
                    .region(Region.of(region))
                    .build();
        }

        LOGGER.info("Amazon S3 client created successfully.");

        return s3Client;
    }
}
