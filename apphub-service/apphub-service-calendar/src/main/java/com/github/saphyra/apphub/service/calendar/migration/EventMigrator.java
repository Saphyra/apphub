package com.github.saphyra.apphub.service.calendar.migration;

import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
class EventMigrator {
    private final DateTimeUtil dateTimeUtil;
    private final MigrationProperties migrationProperties;
    private final EventDao eventDao;
    private final AccessTokenProvider accessTokenProvider;
    private final OccurrenceMigrator occurrenceMigrator;
    private final ErrorReporterService errorReporterService;

    void migrate(DeprecatedEvent deprecatedEvent) {
        try {
            log.info("Migrating event {}", deprecatedEvent.getEventId());

            Event event = migrateEvent(deprecatedEvent);
            AccessTokenHeader accessTokenHeader = AccessTokenHeader.builder()
                .userId(deprecatedEvent.getUserId())
                .build();
            try (AutoCloseable ignore = accessTokenProvider.set(accessTokenHeader)) {
                eventDao.save(event);

                occurrenceMigrator.migrate(event);
            }

            log.info("Migration completed for event {}", deprecatedEvent.getEventId());
        } catch (Exception e) {
            log.error("Failed migrating event {}", deprecatedEvent.getEventId(), e);
            errorReporterService.report("Failed migrating event " + deprecatedEvent.getEventId(), e);
        }
    }

    private Event migrateEvent(DeprecatedEvent deprecatedEvent) {
        return Event.builder()
            .eventId(deprecatedEvent.getEventId())
            .userId(deprecatedEvent.getUserId())
            .repetitionType(deprecatedEvent.getRepetitionType())
            .repetitionData(deprecatedEvent.getRepetitionData())
            .repeatForDays(deprecatedEvent.getRepeat())
            .startDate(deprecatedEvent.getStartDate())
            .endDate(getEndDate(deprecatedEvent))
            .time(deprecatedEvent.getTime())
            .title(deprecatedEvent.getTitle())
            .content(deprecatedEvent.getContent())
            .remindMeBeforeDays(0)
            .build();
    }

    private LocalDate getEndDate(DeprecatedEvent event) {
        if (event.getRepetitionType() == RepetitionType.ONE_TIME) {
            return event.getStartDate();
        }

        return dateTimeUtil.getCurrentDate().plusDays(migrationProperties.getDefaultEndDateOffset().toDays());
    }
}
