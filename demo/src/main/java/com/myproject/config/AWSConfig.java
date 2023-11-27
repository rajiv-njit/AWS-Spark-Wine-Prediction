package com.myproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@PropertySource("classpath:application.properties")
public class AWSConfig {

    @Value("${aws.region}")
    private String region;

    @Value("${aws_access_key_id:#{null}}")
    private String accessKeyId;

    @Value("${aws_secret_access_key:#{null}}")
    private String secretKey;

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        if (accessKeyId != null && secretKey != null) {
            return () -> AwsBasicCredentials.create(accessKeyId, secretKey);
        } else {
            return AwsCredentialsProviderChain.builder()
                    .credentialsProviders(
                            SystemPropertyCredentialsProvider.create(),
                            EnvironmentVariableCredentialsProvider.create()
                    )
                    .build();
        }
    }

    @Bean
    public S3Client s3Client(AwsCredentialsProvider awsCredentialsProvider) {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

    // Getter methods for access key ID and secret key
    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
