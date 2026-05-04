package com.github.saphrya.apphub.service.platform.authorization.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import static com.github.saphrya.apphub.service.platform.authorization.BeanNames.REFRESH_TOKEN_DYNAMO_DB_CLIENT;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Configuration
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class AuthorizationDynamoDbConfig {
    private final AuthorizationProperties properties;

    @Bean(REFRESH_TOKEN_DYNAMO_DB_CLIENT)
    DynamoDbEnhancedClient dynamoDbClient() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
            properties.getDynamoDbAccessKeyId(),
            properties.getDynamoDbSecretKey()
        );

        DynamoDbClientBuilder builder = DynamoDbClient.builder()
            .region(Region.EU_CENTRAL_1)
            .credentialsProvider(StaticCredentialsProvider.create(credentials));

        if (!isBlank(properties.getDynamoDbUrl())) {
            log.info("Overriding DynamoDB url with {}",  properties.getDynamoDbUrl());
            builder = builder.endpointOverride(URI.create(properties.getDynamoDbUrl()));
        }

        DynamoDbClient client = builder.build();

        return DynamoDbEnhancedClient.builder()
            .dynamoDbClient(client)
            .build();
    }
}
