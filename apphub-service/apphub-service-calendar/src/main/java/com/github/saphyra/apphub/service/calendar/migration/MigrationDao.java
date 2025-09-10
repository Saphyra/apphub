package com.github.saphyra.apphub.service.calendar.migration;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.LocalDateEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.LocalTimeEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class MigrationDao {
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_REPETITION_TYPE = "repetition_type";
    public static final String COLUMN_REPETITION_DATA = "repetition_data";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_REPEAT = "repeat";
    public static final String COLUMN_EVENT_ID = "event_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_OCCURRENCE_ID = "occurrence_id";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_NOTE = "note";

    private final JdbcTemplate jdbcTemplate;
    private final StringEncryptor stringEncryptor;
    private final LocalDateEncryptor localDateEncryptor;
    private final LocalTimeEncryptor localTimeEncryptor;
    private final IntegerEncryptor integerEncryptor;
    private final UuidConverter uuidConverter;

    List<DeprecatedEvent> getAllEvents() {
        String sql = "SELECT * FROM calendar.event_old";

        return jdbcTemplate.query(
            sql,
            rs -> {
                List<DeprecatedEvent> events = new java.util.ArrayList<>();
                while (rs.next()) {
                    String eventId = rs.getString(COLUMN_EVENT_ID);
                    String userId = rs.getString(COLUMN_USER_ID);

                    DeprecatedEvent event = DeprecatedEvent.builder()
                        .eventId(uuidConverter.convertEntity(eventId))
                        .userId(uuidConverter.convertEntity(userId))
                        .startDate(localDateEncryptor.decrypt(rs.getString(COLUMN_START_DATE), userId, eventId, "start-date"))
                        .time(localTimeEncryptor.decrypt(rs.getString(COLUMN_TIME), userId, eventId, COLUMN_TIME))
                        .repetitionType(RepetitionType.valueOf(rs.getString(COLUMN_REPETITION_TYPE)))
                        .repetitionData(stringEncryptor.decrypt(rs.getString(COLUMN_REPETITION_DATA), userId, eventId, "repetition-data"))
                        .title(stringEncryptor.decrypt(rs.getString(COLUMN_TITLE), userId, eventId, COLUMN_TITLE))
                        .content(stringEncryptor.decrypt(rs.getString(COLUMN_CONTENT), userId, eventId, COLUMN_CONTENT))
                        .repeat(Optional.ofNullable(integerEncryptor.decrypt(rs.getString(COLUMN_REPEAT), userId, eventId, COLUMN_REPEAT)).filter(integer -> integer > 0).orElse(1))
                        .build();
                    events.add(event);
                }
                return events;
            }
        );
    }

    List<DeprecatedOccurrence> getOccurrences(UUID eventId) {
        String sql = "SELECT * FROM calendar.occurrence_old WHERE event_id='%s'".formatted(eventId);

        return jdbcTemplate.query(
            sql,
            rs -> {
                List<DeprecatedOccurrence> occurrences = new java.util.ArrayList<>();
                while (rs.next()) {
                    String occurrenceId = rs.getString(COLUMN_OCCURRENCE_ID);
                    String userId = rs.getString(COLUMN_USER_ID);

                    String status = rs.getString(COLUMN_STATUS);
                    DeprecatedOccurrence occurrence = DeprecatedOccurrence.builder()
                        .occurrenceId(uuidConverter.convertEntity(occurrenceId))
                        .eventId(uuidConverter.convertEntity(rs.getString(COLUMN_EVENT_ID)))
                        .userId(uuidConverter.convertEntity(userId))
                        .date(localDateEncryptor.decrypt(rs.getString(COLUMN_DATE), userId, occurrenceId, COLUMN_DATE))
                        .time(localTimeEncryptor.decrypt(rs.getString(MigrationDao.COLUMN_TIME), userId, occurrenceId, COLUMN_TIME))
                        .status("VIRTUAL".equals(status) ? OccurrenceStatus.PENDING : OccurrenceStatus.valueOf(status))
                        .note(stringEncryptor.decrypt(rs.getString(COLUMN_NOTE), userId, occurrenceId, COLUMN_NOTE))
                        .build();
                    occurrences.add(occurrence);
                }
                return occurrences;
            }
        );
    }
}
