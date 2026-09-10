package com.github.saphyra.apphub.service.feature.calendar.domain.event.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepository;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepositoryContext;
import com.github.saphyra.apphub.lib.dynamodb.MonitoringFunctionality;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
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
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_EVENT;

@Component
@Slf4j
@Profile("!test")
class EventDecryptionMigrator extends DynamoDbRepository {
    static final String EVENT_DECRYPTION = "event-decryption";

    private final DeprecatedEventConverter decryptorConverter;
    private final EventConverter converter;
    private final EventMapper mapper;
    private final AccessTokenProvider accessTokenProvider;
    private final UuidConverter uuidConverter;
    private final ErrorReporterService errorReporterService;

    EventDecryptionMigrator(
        CalendarDynamoDbConfiguration configuration,
        DynamoDbRepositoryContext context,
        DeprecatedEventConverter decryptorConverter,
        EventConverter converter,
        EventMapper mapper,
        AccessTokenProvider accessTokenProvider,
        UuidConverter uuidConverter,
        ErrorReporterService errorReporterService
    ) {
        super(configuration.getCalendarTableName(), context);
        this.decryptorConverter = decryptorConverter;
        this.converter = converter;
        this.mapper = mapper;
        this.accessTokenProvider = accessTokenProvider;
        this.uuidConverter = uuidConverter;
        this.errorReporterService = errorReporterService;
    }

    @PostConstruct
    void migrate() {
        Map<String, AttributeValue> lock = Map.of(
            COLUMN_PK, AttributeValue.builder().s(MIGRATION).build(),
            COLUMN_SK, AttributeValue.builder().s(EVENT_DECRYPTION).build()
        );
        if (getItem(lock, MonitoringFunctionality.UNMONITORED).isPresent()) {
            log.info("Event decryption migration already performed.");
            return;
        }

        try {
            log.info("Initiating Event decryption migration...");

            ScanRequest request = ScanRequest.builder()
                .filterExpression("begins_with(#sk, :sk)")
                .expressionAttributeNames(Map.of("#sk", COLUMN_SK))
                .expressionAttributeValues(Map.of(":sk", AttributeValue.builder().s(PREFIX_EVENT).build()))
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

            log.info("Event decryption migration completed. Migrated {} events.", result.size());
        } catch (Exception e) {
            errorReporterService.report("Event decryption migration failed.", e);
        }

        putItem(lock, MonitoringFunctionality.UNMONITORED);
    }

    @SneakyThrows
    private Event decrypt(EventEntity entity) {
        UUID userId = uuidConverter.convertEntity(entity.getUserId());
        try (var _ = accessTokenProvider.set(userId)) {
            return decryptorConverter.convertEntity(entity);
        }
    }
}
