package com.github.saphyra.apphub.service.feature.calendar.domain.label.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepository;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepositoryContext;
import com.github.saphyra.apphub.lib.dynamodb.MonitoringFunctionality;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDynamoDbConfiguration;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.MIGRATION;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_LABEL;

@Component
@Slf4j
@Profile("!test")
//TODO unit test
class LabelDecryptionMigrator extends DynamoDbRepository {
    private static final String LABEL_DECRYPTION = "label-decryption";

    private final UuidConverter uuidConverter;
    private final AccessTokenProvider accessTokenProvider;
    private final DeprecatedLabelConverter decryptorConverter;
    private final LabelConverter converter;
    private final LabelMapper mapper;

    LabelDecryptionMigrator(
        CalendarDynamoDbConfiguration configuration,
        DynamoDbRepositoryContext context,
        UuidConverter uuidConverter,
        AccessTokenProvider accessTokenProvider,
        DeprecatedLabelConverter decryptorConverter,
        LabelConverter converter,
        LabelMapper mapper
    ) {
        super(configuration.getCalendarTableName(), context);
        this.uuidConverter = uuidConverter;
        this.accessTokenProvider = accessTokenProvider;
        this.decryptorConverter = decryptorConverter;
        this.converter = converter;
        this.mapper = mapper;
    }

    @PostConstruct
    void migrate() {
        Map<String, AttributeValue> lock = Map.of(
            COLUMN_PK, AttributeValue.builder().s(MIGRATION).build(),
            COLUMN_SK, AttributeValue.builder().s(LABEL_DECRYPTION).build()
        );
        if (getItem(lock, MonitoringFunctionality.UNMONITORED).isPresent()) {
            log.info("Label decryption migration already performed.");
            return;
        }

        log.info("Initiating Label decryption migration...");

        ScanRequest request = ScanRequest.builder()
            .filterExpression("begins_with(#sk, :sk)")
            .expressionAttributeNames(Map.of("#sk", COLUMN_SK))
            .expressionAttributeValues(Map.of(":sk", AttributeValue.builder().s(PREFIX_LABEL).build()))
            .build();

        List<Map<String, AttributeValue>> result = scan(request, MonitoringFunctionality.UNMONITORED);

        List<WriteRequest> decrypted = result.stream()
            .map(mapper::convertEntity)
            .map(this::decrypt)
            .map(converter::convertDomain)
            .map(mapper::convertDomain)
            .map(item -> PutRequest.builder().item(item).build())
            .map(putRequest -> WriteRequest.builder().putRequest(putRequest).build())
            .toList();
        batchWrite(decrypted, MonitoringFunctionality.UNMONITORED);

        putItem(lock, MonitoringFunctionality.UNMONITORED);

        log.info("Label decryption migration completed. Migrated {} labels.", result.size());
    }

    @SneakyThrows
    private Label decrypt(LabelEntity entity) {
        UUID userId = uuidConverter.convertEntity(entity.getUserId());
        try (var _ = accessTokenProvider.set(userId)) {
            return decryptorConverter.convertEntity(entity);
        }
    }
}
