package com.github.saphyra.apphub.service.feature.calendar.domain;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CommonCalendarDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEvent;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.deprecated_dao.DeprecatedEventLabelMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.deprecated_dao.DeprecatedEventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.deprecated_dao.DeprecatedLabelDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrenceDao;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
//TODO unit test
class CalendarMigrationService {
    private final JdbcTemplate jdbcTemplate;
    private final DeprecatedLabelDao deprecatedLabelDao;
    private final LabelDao labelDao;
    private final AccessTokenProvider accessTokenProvider;
    private final UuidConverter uuidConverter;
    private final DeprecatedEventDao deprecatedEventDao;
    private final DeprecatedOccurrenceDao deprecatedOccurrenceDao;
    private final DeprecatedEventLabelMappingDao deprecatedEventLabelMappingDao;
    private final CommonCalendarDao commonCalendarDao;

    CalendarMigrationService(
        JdbcTemplate jdbcTemplate,
        DeprecatedLabelDao deprecatedLabelDao,
        LabelDao labelDao,
        AccessTokenProvider accessTokenProvider,
        UuidConverter uuidConverter,
        DeprecatedEventDao deprecatedEventDao,
        DeprecatedOccurrenceDao deprecatedOccurrenceDao,
        DeprecatedEventLabelMappingDao deprecatedEventLabelMappingDao,
        CommonCalendarDao commonCalendarDao
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.deprecatedLabelDao = deprecatedLabelDao;
        this.labelDao = labelDao;
        this.accessTokenProvider = accessTokenProvider;
        this.uuidConverter = uuidConverter;
        this.deprecatedEventDao = deprecatedEventDao;
        this.deprecatedOccurrenceDao = deprecatedOccurrenceDao;
        this.deprecatedEventLabelMappingDao = deprecatedEventLabelMappingDao;
        this.commonCalendarDao = commonCalendarDao;
    }

    @PostConstruct
    @Transactional
    void migrate() {
        log.info("Calendar migration started...");

        migrateLabels();
        migrateEvents();

        log.info("Calendar migration finished.");
    }

    private void migrateEvents() {
        log.info("Migrating events...");

        getEventUserIds()
            .forEach(this::migrateEvents);

        log.info("Events migrated.");
    }

    @SneakyThrows
    private void migrateEvents(UUID userId) {
        log.info("Migrating events for user {}", userId);

        try (var _ = accessTokenProvider.set(AccessToken.builder().userId(userId).build())) {
            deprecatedEventDao.getByUserId(userId)
                .forEach(this::migrateEvent);
        }

        log.info("Events migrated for user {}", userId);
    }

    private void migrateEvent(DeprecatedEvent oldEvent) {
        log.info("Migrating event {}", oldEvent.getEventId());

        Event event = convertEvent(oldEvent);
        List<Occurrence> occurrences = getOccurrences(event);
        List<UUID> labelIds = getLabels(event);

        commonCalendarDao.saveNewEvent(event, occurrences, labelIds);
        deprecatedEventDao.delete(oldEvent);

        log.info("Event {} migrated", oldEvent.getEventId());
    }

    private List<UUID> getLabels(Event event) {
        return deprecatedEventLabelMappingDao.getByEventId(event.getEventId())
            .stream()
            .map(DeprecatedEventLabelMapping::getLabelId)
            .toList();
    }

    private List<Occurrence> getOccurrences(Event event) {
        return deprecatedOccurrenceDao.getByUserId(event.getUserId())
            .stream()
            .map(oldOccurrence -> Occurrence.builder()
                .userId(oldOccurrence.getUserId())
                .eventId(oldOccurrence.getEventId())
                .occurrenceId(oldOccurrence.getOccurrenceId())
                .date(oldOccurrence.getDate())
                .time(oldOccurrence.getTime())
                .status(oldOccurrence.getStatus())
                .note(oldOccurrence.getNote())
                .remindMeBeforeDays(oldOccurrence.getRemindMeBeforeDays())
                .reminded(oldOccurrence.getReminded())
                .build())
            .toList();
    }

    private Event convertEvent(DeprecatedEvent oldEvent) {
        return Event.builder()
            .eventId(oldEvent.getEventId())
            .userId(oldEvent.getUserId())
            .repetitionType(oldEvent.getRepetitionType())
            .repetitionData(oldEvent.getRepetitionData())
            .repeatForDays(oldEvent.getRepeatForDays())
            .startDate(oldEvent.getStartDate())
            .endDate(oldEvent.getEndDate())
            .time(oldEvent.getTime())
            .title(oldEvent.getTitle())
            .content(oldEvent.getContent())
            .remindMeBeforeDays(oldEvent.getRemindMeBeforeDays())
            .expirationNotified(oldEvent.isExpirationNotified())
            .archived(oldEvent.isArchived())
            .build();
    }

    private List<UUID> getEventUserIds() {
        String sql = "SELECT user_id FROM calendar.event GROUP BY user_id";

        return jdbcTemplate.query(
            sql,
            (rs, _) -> uuidConverter.convertEntity(rs.getString("user_id"))
        );
    }

    private void migrateLabels() {
        log.info("Migrating labels...");

        getLabelUserIds()
            .forEach(this::migrateLabels);

        log.info("Labels migrated.");
    }

    @SneakyThrows
    private void migrateLabels(UUID userId) {
        log.info("Migrating labels for user {}", userId);
        try (var _ = accessTokenProvider.set(AccessToken.builder().userId(userId).build())) {
            deprecatedLabelDao.getByUserId(userId)
                .forEach(oldLabel -> {
                    log.info("Migrating label {}", oldLabel.getLabelId());
                    Label label = Label.builder()
                        .labelId(oldLabel.getLabelId())
                        .label(oldLabel.getLabel())
                        .build();
                    labelDao.save(userId, label);
                    deprecatedLabelDao.delete(oldLabel);
                    log.info("Label {} migrated.", oldLabel.getLabelId());
                });
        }
        log.info("Labels migrated for user {}", userId);
    }

    private List<UUID> getLabelUserIds() {
        String sql = "SELECT user_id FROM calendar.label GROUP BY user_id";

        return jdbcTemplate.query(
            sql,
            (rs, _) -> uuidConverter.convertEntity(rs.getString("user_id"))
        );
    }
}
