import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
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
    public AmazonS3 amazonS3Client() {
        AmazonS3 s3Client;

        if (accessKeyId != null && secretKey != null) {
            // Use explicit credentials if available
            AWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyId, secretKey);
            AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);

            s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(credentialsProvider)
                    .withRegion(region)
                    .build();
        } else {
            // Use default credentials provider
            s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(region)
                    .build();
        }

        LOGGER.info("AmazonS3 client created successfully.");

        return s3Client;
    }
}
