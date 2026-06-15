package com.github.saphyra.apphub.service.feature.calendar.domain.label.dao;

import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepository;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepositoryContext;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDynamoDbConfiguration;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarMonitoringFunctionality;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
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
    LabelRepository(CalendarDynamoDbConfiguration configuration, DynamoDbRepositoryContext context) {
        super(configuration.getTableName(), context);
    }

    List<LabelEntity> getByLabelIds(String userId, List<String> labelIds) {
        List<Map<String, AttributeValue>> keys = labelIds.stream()
            .map(id -> Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_LABEL + id).build()
            ))
            .toList();

        return batchGetItem(keys, CalendarMonitoringFunctionality.GET_LABELS_BY_IDS)
            .stream()
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

        putItem(request, CalendarMonitoringFunctionality.SAVE_LABEL);
    }

    Optional<LabelEntity> findById(String userId, String labelId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_LABEL + labelId).build()
            ))
            .build();

        return getItem(request, CalendarMonitoringFunctionality.FIND_LABEL_BY_ID)
            .map(item ->
                LabelEntity.builder()
                    .labelId(item.get(COLUMN_SK).s().substring(PREFIX_LABEL.length()))
                    .label(item.get(COLUMN_LABEL).s())
                    .build()
            );
    }

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

        return query(request, CalendarMonitoringFunctionality.GET_LABELS_BY_USER_ID)
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

        deleteItem(request, CalendarMonitoringFunctionality.DELETE_LABEL);
    }
}
