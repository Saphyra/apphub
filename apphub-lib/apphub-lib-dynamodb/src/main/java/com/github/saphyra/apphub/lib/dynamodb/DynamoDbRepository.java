package com.github.saphyra.apphub.lib.dynamodb;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public abstract class DynamoDbRepository {
    @Getter
    private final DynamoDbClient client;
    private final DynamoDbRepositoryQueryUtil queryUtil;
    private final DynamoDbRepositoryScanUtil scanUtil;
    private final DynamoDbRepositoryBatchWriteUtil batchWriteUtil;
    private final DynamoDbRepositoryBatchGetItemUtil batchGetItemUtil;
    private final DynamoDbRepositoryPutItemUtil putItemUtil;
    private final DynamoDbRepositoryGetItemUtil getItemUtil;
    private final DynamoDbRepositoryDeleteItemUtil deleteItemUtil;

    protected final String tableName;

    protected DynamoDbRepository(String tableName, DynamoDbRepositoryContext context) {
        this.client = context.getClient();

        this.tableName = tableName;
        this.queryUtil = context.getQueryUtil();
        this.scanUtil = context.getScanUtil();
        this.batchWriteUtil = context.getBatchWriteUtil();
        this.batchGetItemUtil = context.getBatchGetItemUtil();
        this.putItemUtil = context.getPutItemUtil();
        this.getItemUtil = context.getGetItemUtil();
        this.deleteItemUtil = context.getDeleteItemUtil();
    }

    protected void putItem(Map<String, AttributeValue> item, MonitoringFunctionality monitoringFunctionality) {
        PutItemRequest request = PutItemRequest.builder()
            .item(item)
            .build();

        putItemUtil.putItem(tableName, request, monitoringFunctionality.assemble(tableName));
    }

    protected void putItem(PutItemRequest request, MonitoringFunctionality monitoringFunctionality) {
        putItemUtil.putItem(tableName, request, monitoringFunctionality.assemble(tableName));
    }

    protected List<Map<String, AttributeValue>> query(QueryRequest queryRequest, MonitoringFunctionality monitoringFunctionality) {
        return queryUtil.query(tableName, queryRequest, monitoringFunctionality.assemble(tableName));
    }

    protected List<Map<String, AttributeValue>> scan(ScanRequest scanRequest, MonitoringFunctionality monitoringFunctionality) {
        return scanUtil.scan(tableName, scanRequest, monitoringFunctionality.assemble(tableName));
    }

    protected void batchWrite(List<WriteRequest> requests, MonitoringFunctionality monitoringFunctionality) {
        batchWriteUtil.batchWrite(tableName, requests, monitoringFunctionality.assemble(tableName));
    }

    protected List<Map<String, AttributeValue>> batchGetItem(List<Map<String, AttributeValue>> keys, MonitoringFunctionality monitoringFunctionality) {
        return batchGetItemUtil.batchGetItem(tableName, keys, monitoringFunctionality.assemble(tableName));
    }

    protected Optional<Map<String, AttributeValue>> getItem(Map<String, AttributeValue> key, MonitoringFunctionality monitoringFunctionality) {
        GetItemRequest request = GetItemRequest.builder()
            .key(key)
            .build();

        return getItem(request, monitoringFunctionality);
    }

    protected Optional<Map<String, AttributeValue>> getItem(GetItemRequest request, MonitoringFunctionality monitoringFunctionality) {
        return getItemUtil.getItem(tableName, request, monitoringFunctionality.assemble(tableName));
    }

    protected void deleteItem(Map<String, AttributeValue> key, MonitoringFunctionality monitoringFunctionality) {
        DeleteItemRequest request = DeleteItemRequest.builder()
            .key(key)
            .build();

        deleteItemUtil.deleteItem(tableName, request, monitoringFunctionality.assemble(tableName));
    }

    protected void deleteItem(DeleteItemRequest request, MonitoringFunctionality monitoringFunctionality) {
        deleteItemUtil.deleteItem(tableName, request, monitoringFunctionality.assemble(tableName));
    }
}
