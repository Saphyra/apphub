package com.github.saphyra.apphub.service.feature.calendar.domain.label.dao;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepository;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDynamoDbConfiguration;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.KeysAndAttributes;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_LABEL;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_LABEL;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_USER;

@Component
class LabelRepository extends DynamoDbRepository {
    private final DynamoDbClient client;
    private final String tableName;

    LabelRepository(DynamoDbClient client, CalendarDynamoDbConfiguration configuration) {
        this.client = client;
        this.tableName = configuration.getTableName();
    }

    List<LabelEntity> getByLabelIds(String userId, List<String> labelIds) {
        if (labelIds.size() > Constants.DYNAMO_DB_QUERY_MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Batch size cannot be greater than " + Constants.DYNAMO_DB_QUERY_MAX_BATCH_SIZE);
        }

        List<Map<String, AttributeValue>> keys = labelIds.stream()
            .map(id -> Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_LABEL + id).build()
            ))
            .toList();

        BatchGetItemRequest request = BatchGetItemRequest.builder()
            .requestItems(Map.of(
                tableName,
                KeysAndAttributes.builder()
                    .keys(keys)
                    .build()
            ))
            .build();

        //TODO handle unprocessed keys
        return client.batchGetItem(request)
            .responses()
            .values()
            .stream()
            .flatMap(List::stream)
            .map(item -> LabelEntity.builder()
                .labelId(item.get(COLUMN_SK).s().substring(PREFIX_LABEL.length()))
                .label(item.get(COLUMN_LABEL).s())
                .build())
            .toList();
    }

    void save(String userId, LabelEntity label) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_LABEL + label.getLabelId()).build(),
                COLUMN_LABEL, AttributeValue.builder().s(label.getLabel()).build()
            ))
            .build();

        client.putItem(request);
    }

    Optional<LabelEntity> findById(String userId, String labelId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_LABEL + labelId).build()
            ))
            .build();

        return Optional.ofNullable(client.getItem(request))
            .filter(GetItemResponse::hasItem)
            .map(GetItemResponse::item)
            .map(item ->
                LabelEntity.builder()
                    .labelId(item.get(COLUMN_SK).s().substring(PREFIX_LABEL.length()))
                    .label(item.get(COLUMN_LABEL).s())
                    .build()
            );
    }

    //TODO handle lastEvaluatedKey
    List<LabelEntity> getByUserId(String userId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("#pk = :userId AND begins_with(#sk, :prefix)")
            .expressionAttributeNames(Map.of(
                "#pk", COLUMN_PK,
                "#sk", COLUMN_SK
            ))
            .expressionAttributeValues(Map.of(
                ":userId", AttributeValue.builder().s(PREFIX_USER + userId).build(),
                ":prefix", AttributeValue.builder().s(PREFIX_LABEL).build()
            ))
            .build();

        return client.query(request)
            .items()
            .stream()
            .map(item -> LabelEntity.builder()
                .labelId(item.get(COLUMN_SK).s().substring(PREFIX_LABEL.length()))
                .label(item.get(COLUMN_LABEL).s())
                .build())
            .toList();
    }

    void delete(String userId, String labelId) {
        DeleteItemRequest request = DeleteItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_LABEL + labelId).build()
            ))
            .build();

        client.deleteItem(request);
    }
}
