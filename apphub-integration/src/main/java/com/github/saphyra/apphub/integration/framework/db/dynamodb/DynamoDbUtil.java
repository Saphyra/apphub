package com.github.saphyra.apphub.integration.framework.db.dynamodb;

import com.github.saphyra.apphub.integration.core.TestConfiguration;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

import java.net.URI;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
public class DynamoDbUtil {
    private static DynamoDbClient dynamoDbClient;

    public static void checkConnection() {
        getClient()
            .listTables();
    }

    public synchronized static DynamoDbClient getClient() {
        if (isNull(dynamoDbClient)) {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(TestConfiguration.DYNAMO_DB_ACCESS_KEY_ID, TestConfiguration.DYNAMO_DB_SECRET_KEY);

            DynamoDbClientBuilder builder = DynamoDbClient.builder()
                .region(Region.of(TestConfiguration.DYNAMO_DB_REGION))
                .credentialsProvider(StaticCredentialsProvider.create(credentials));

            if (!isBlank(TestConfiguration.DYNAMO_DB_HOST)) {
                builder = builder.endpointOverride(URI.create("http://" + TestConfiguration.DYNAMO_DB_HOST));
            }

            dynamoDbClient = builder.build();
        }

        return dynamoDbClient;
    }
}
