package com.github.saphyra.apphub.service.feature.task_manager.domain.organization.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepository;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepositoryContext;
import com.github.saphyra.apphub.service.feature.task_manager.configuration.TaskManagerDynamoDbConfiguration;
import com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerMonitoringFunctionality;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_PK;

@Component
@Slf4j
@Profile("!test")
class OrganizationRepository extends DynamoDbRepository {
    private final OrganizationMapper mapper;
    private final UuidConverter uuidConverter;

    OrganizationRepository(TaskManagerDynamoDbConfiguration configuration, DynamoDbRepositoryContext context, OrganizationMapper mapper, UuidConverter uuidConverter) {
        super(configuration.getOrganizationTableName(), context);
        this.mapper = mapper;
        this.uuidConverter = uuidConverter;
    }

    void save(Organization organization) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(mapper.convertDomain(organization))
            .build();

        putItem(request, TaskManagerMonitoringFunctionality.SAVE_ORGANIZATION);
    }

    List<Organization> getByIds(List<UUID> organizationIds) {
        List<Map<String, AttributeValue>> keys = organizationIds.stream()
            .map(id -> Map.of(COLUMN_PK, AttributeValue.builder().s(uuidConverter.convertDomain(id)).build()))
            .toList();

        return batchGetItem(keys, TaskManagerMonitoringFunctionality.GET_ORGANIZATIONS)
            .stream()
            .map(mapper::convertEntity)
            .toList();
    }

    Optional<Organization> findById(UUID organizationId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(COLUMN_PK, AttributeValue.builder().s(uuidConverter.convertDomain(organizationId)).build()))
            .build();

        return getItem(request, TaskManagerMonitoringFunctionality.GET_ORGANIZATION)
            .map(mapper::convertEntity);
    }

    @PostConstruct
    void createTable() {
        try {
            getClient()
                .describeTable(builder -> builder.tableName(tableName));
            log.info("DynamoDb table '{}' already exists", tableName);
        } catch (ResourceNotFoundException e) {
            log.info("Creating DynamoDb table '{}'", tableName);

            CreateTableRequest createTableRequest = CreateTableRequest.builder()
                .tableName(tableName)
                .attributeDefinitions(
                    AttributeDefinition.builder()
                        .attributeName(COLUMN_PK)
                        .attributeType(ScalarAttributeType.S)
                        .build()
                )
                .keySchema(
                    KeySchemaElement.builder()
                        .attributeName(COLUMN_PK)
                        .keyType(KeyType.HASH)
                        .build()
                )
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build();

            getClient()
                .createTable(createTableRequest);

            getClient()
                .waiter()
                .waitUntilTableExists(builder -> builder.tableName(tableName));
        }
    }
}
