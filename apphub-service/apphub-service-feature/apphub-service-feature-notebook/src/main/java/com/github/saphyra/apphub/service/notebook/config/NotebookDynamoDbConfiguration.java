package com.github.saphyra.apphub.service.notebook.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

import java.net.URI;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Configuration
@Slf4j
public class NotebookDynamoDbConfiguration {
    @Getter
    private final String tableName;

    NotebookDynamoDbConfiguration(@Value("${aws.dynamoDb.listItem.tableName}") String tableName) {
        this.tableName = tableName;
    }

    @Bean
    @Profile("!test")
    DynamoDbClient dynamoDbClient(
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

        return builder.build();
    }
}
