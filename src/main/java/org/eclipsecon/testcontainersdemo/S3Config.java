package org.eclipsecon.testcontainersdemo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;
import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "s3")
public class S3Config {
    private String rootUser;
    private String rootPassword;
    private URI endpointUrl;
    private Region region = Region.US_EAST_1;

    public void setRootUser(String rootUser) {
        this.rootUser = rootUser;
    }

    public void setRootPassword(String rootPassword) {
        this.rootPassword = rootPassword;
    }

    public void setEndpointUrl(URI endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    @Bean
    public S3Client createS3Client() {
        final var serviceConfiguration = S3Configuration.builder()
                .checksumValidationEnabled(false)
                .chunkedEncodingEnabled(true)
                .build();

        return S3Client.builder()
                .forcePathStyle(true) // see https://stackoverflow.com/a/72221858
                .credentialsProvider(credentialsProvider())
                .serviceConfiguration(serviceConfiguration)
                .region(region)
                .endpointOverride(endpointUrl)
                .build();
    }

    @Bean
    public S3AsyncClient createS3AsyncClient() {
        final var httpClient = NettyNioAsyncHttpClient.builder()
                .writeTimeout(Duration.ZERO)
                .build();

        final var serviceConfiguration = S3Configuration.builder()
                .checksumValidationEnabled(false)
                .chunkedEncodingEnabled(true)
                .build();

        return S3AsyncClient.builder()
                .httpClient(httpClient)
                .credentialsProvider(credentialsProvider())
                .region(region)
                .endpointOverride(endpointUrl)
                .serviceConfiguration(serviceConfiguration).build();
    }

    private StaticCredentialsProvider credentialsProvider() {
        final AwsBasicCredentials credentials = AwsBasicCredentials.create(rootUser, rootPassword);
        return StaticCredentialsProvider.create(credentials);
    }
}