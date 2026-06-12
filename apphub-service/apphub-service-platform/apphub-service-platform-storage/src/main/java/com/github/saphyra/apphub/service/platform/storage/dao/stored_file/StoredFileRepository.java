package com.github.saphyra.apphub.service.platform.storage.dao.stored_file;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.TimeToLiveSpecification;
import software.amazon.awssdk.services.dynamodb.model.UpdateTimeToLiveRequest;

import java.util.List;
import java.util.Optional;

@Component
@Profile("!test")
@Slf4j
class StoredFileRepository {
    private final DynamoDbEnhancedClient client;
    private final String tableName;

    StoredFileRepository(
        DynamoDbEnhancedClient client,
        @Value("${aws.dynamoDb.stored_file.tableName}") String tableName
    ) {
        this.client = client;
        this.tableName = tableName;
    }

    List<StoredFileEntity> getByUserId(String userId) {
        Key key = Key.builder()
            .partitionValue(userId)
            .build();

        QueryConditional query = QueryConditional.keyEqualTo(key);

        return getTable()
            .query(query)
            .items()
            .stream()
            .toList();
    }

    Optional<StoredFileEntity> findById(String userId, String storedFileId) {
        Key key = Key.builder()
            .partitionValue(userId)
            .sortValue(storedFileId)
            .build();

        return Optional.ofNullable(getTable().getItem(key));
    }

    void delete(String userId, String storedFileId) {
        Key key = Key.builder()
            .partitionValue(userId)
            .sortValue(storedFileId)
            .build();

        getTable().deleteItem(key);
    }

    void save(StoredFileEntity storedFileEntity) {
        getTable().putItem(storedFileEntity);
    }

    @PostConstruct
    void createTable() {
        DynamoDbClient dynamoDbClient = client.dynamoDbClient();
        try {
            dynamoDbClient.describeTable(builder -> builder.tableName(tableName));
            log.info("DynamoDb table {} already exists", tableName);
        } catch (ResourceNotFoundException e) {
            log.info("Creating DynamoDb table {}", tableName);

            getTable().createTable();

            dynamoDbClient.waiter()
                .waitUntilTableExists(builder -> builder.tableName(tableName));

            UpdateTimeToLiveRequest request = UpdateTimeToLiveRequest.builder()
                .tableName(tableName)
                .timeToLiveSpecification(TimeToLiveSpecification.builder()
                    .attributeName("expiration")
                    .enabled(true)
                    .build())
                .build();

            dynamoDbClient.updateTimeToLive(request);
        }
    }

    private DynamoDbTable<StoredFileEntity> getTable() {
        return client.table(tableName, TableSchema.fromBean(StoredFileEntity.class));
    }
}
