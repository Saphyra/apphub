package com.github.saphyra.apphub.service.platform.storage.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

import java.net.URI;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Configuration
@Slf4j
@Profile("!test")
public class StorageDynamoDbConfiguration {
    @Bean
    DynamoDbEnhancedClient dynamoDbClient(
        @Value("${aws.dynamoDb.accessKeyId}") String accessKeyId,
        @Value("${aws.dynamoDb.secretKey}") String secretKey,
        @Value("${aws.dynamoDb.url}") String dynamoDbUrl
    ) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretKey);

        DynamoDbClientBuilder builder = DynamoDbClient.builder()
            .region(Region.EU_CENTRAL_1)
            .credentialsProvider(StaticCredentialsProvider.create(credentials));

        if (!isBlank(dynamoDbUrl)) {
            log.info("Overriding DynamoDB url with {}", dynamoDbUrl);
            builder = builder.endpointOverride(URI.create(dynamoDbUrl));
        }

        DynamoDbClient client = builder.build();

        return DynamoDbEnhancedClient.builder()
            .dynamoDbClient(client)
            .build();
    }
}
