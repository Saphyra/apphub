package com.github.saphrya.apphub.service.platform.authorization.dao.refresh_token;

import com.github.saphrya.apphub.service.platform.authorization.config.AuthorizationProperties;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.TimeToLiveSpecification;
import software.amazon.awssdk.services.dynamodb.model.UpdateTimeToLiveRequest;

import java.util.List;
import java.util.Optional;

import static com.github.saphrya.apphub.service.platform.authorization.BeanNames.REFRESH_TOKEN_DYNAMO_DB_CLIENT;

@SuppressWarnings("resource")
@Component
@Slf4j
@Profile("!test")
class DynamoDbRefreshTokenRepository implements RefreshTokenRepository {
    private final DynamoDbEnhancedClient client;
    private final String tableName;

    DynamoDbRefreshTokenRepository(
        @Qualifier(REFRESH_TOKEN_DYNAMO_DB_CLIENT) DynamoDbEnhancedClient client,
        AuthorizationProperties properties
    ) {
        this.client = client;
        this.tableName = properties.getRefreshTokenTableName();
    }

    @Override
    public void save(RefreshTokenEntity refreshToken) {
        getTable().putItem(refreshToken);
    }

    @Override
    public void delete(String userId, String refreshTokenId) {
        Key key = Key.builder()
            .partitionValue(userId)
            .sortValue(refreshTokenId)
            .build();

        getTable().deleteItem(key);
    }

    @Override
    public Optional<RefreshTokenEntity> findById(String userId, String refreshTokenId) {
        Key key = Key.builder()
            .partitionValue(userId)
            .sortValue(refreshTokenId)
            .build();
        return Optional.ofNullable(getTable().getItem(key));
    }

    private DynamoDbTable<RefreshTokenEntity> getTable() {
        return client.table(tableName, TableSchema.fromBean(RefreshTokenEntity.class));
    }

    @Override
    public List<RefreshTokenEntity> getByUserId(String userId) {
        DynamoDbTable<RefreshTokenEntity> table = getTable();

        return table.query(r -> r.queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(userId))))
            .stream()
            .flatMap(page -> page.items().stream())
            .toList();
    }

    @Override
    public void delete(List<RefreshTokenEntity> entities) {
        if (entities.size() > Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Batch write can only handle 25 items at once. Input size: " + entities.size());
        }

        BatchWriteItemEnhancedRequest.Builder builder = BatchWriteItemEnhancedRequest.builder();

        entities.forEach(entity -> builder.addWriteBatch(
            WriteBatch.builder(RefreshTokenEntity.class)
                .mappedTableResource(getTable())
                .addDeleteItem(
                    Key.builder()
                        .partitionValue(entity.getUserId())
                        .sortValue(entity.getRefreshTokenId())
                        .build()
                )
                .build())
        );

        client.batchWriteItem(builder.build());
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
}
