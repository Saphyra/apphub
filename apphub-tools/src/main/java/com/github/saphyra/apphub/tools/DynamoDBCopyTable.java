package com.github.saphyra.apphub.tools;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.Map;
import java.util.Scanner;

public class DynamoDBCopyTable {
    private static final Region REGION = Region.EU_CENTRAL_1;

    static void main(String[] args) {
        TableConfig source;
        TableConfig destination;

        if (args.length == 6) {
            source = new TableConfig(args[0], args[1], args[2]);
            destination = new TableConfig(args[3], args[4], args[5]);
        } else {
            Scanner scanner = new Scanner(System.in);
            source = promptForConfig(scanner, "SOURCE");
            destination = promptForConfig(scanner, "DESTINATION");
        }

        try (DynamoDbClient sourceClient = createClient(source);
             DynamoDbClient destinationClient = createClient(destination)) {
            long copiedItems = copyTable(sourceClient, destinationClient, source.tableName(), destination.tableName());
            System.out.printf("Copied %d items from %s to %s.%n", copiedItems, source.tableName(), destination.tableName());
        } catch (DynamoDbException e) {
            System.err.println("Failed to copy table: " + e.getMessage());
            System.exit(1);
        }
    }

    private static TableConfig promptForConfig(Scanner scanner, String label) {
        System.out.println(label + " table configuration");
        String accessKeyId = readRequired(scanner, label + " accessKeyId");
        String secretKey = readRequired(scanner, label + " secretKey");
        String tableName = readRequired(scanner, label + " tableName");
        return new TableConfig(accessKeyId, secretKey, tableName);
    }

    private static String readRequired(Scanner scanner, String prompt) {
        System.out.print(prompt + ": ");
        String value = scanner.nextLine();
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(prompt + " cannot be empty.");
        }
        return value.trim();
    }

    private static DynamoDbClient createClient(TableConfig config) {
        return DynamoDbClient.builder()
            .region(REGION)
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(config.accessKeyId(), config.secretKey())
            ))
            .build();
    }

    private static long copyTable(
        DynamoDbClient sourceClient,
        DynamoDbClient destinationClient,
        String sourceTableName,
        String destinationTableName
    ) {
        long total = 0;
        Map<String, AttributeValue> lastEvaluatedKey = null;

        do {
            ScanRequest.Builder requestBuilder = ScanRequest.builder()
                .tableName(sourceTableName);

            if (lastEvaluatedKey != null && !lastEvaluatedKey.isEmpty()) {
                requestBuilder.exclusiveStartKey(lastEvaluatedKey);
            }

            ScanResponse response = sourceClient.scan(requestBuilder.build());

            for (Map<String, AttributeValue> item : response.items()) {
                destinationClient.putItem(PutItemRequest.builder()
                    .tableName(destinationTableName)
                    .item(item)
                    .build());
                total++;
            }

            lastEvaluatedKey = response.lastEvaluatedKey();
        } while (lastEvaluatedKey != null && !lastEvaluatedKey.isEmpty());

        return total;
    }

    private record TableConfig(String accessKeyId, String secretKey, String tableName) {
    }
}
