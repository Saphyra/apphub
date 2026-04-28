package com.github.saphyra.apphub.service.platform.storage.client.s3;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@S3ClientEnabled
class S3Configuration {
    @Bean
    S3Client s3Client(S3Properties properties) {
        AwsBasicCredentials credentials = AwsBasicCredentials.builder()
            .accessKeyId(properties.getAccessKeyId())
            .secretAccessKey(properties.getSecretKey())
            .build();
        return S3Client.builder()
            .region(Region.EU_CENTRAL_1)
            .credentialsProvider(() -> credentials)
            .build();
    }
}
