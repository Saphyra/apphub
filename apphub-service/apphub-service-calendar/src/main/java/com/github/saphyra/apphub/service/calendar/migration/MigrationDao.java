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
//TODO unit test
class MigrationDao {
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
                    String eventId = rs.getString("event_id");
                    String userId = rs.getString("user_id");

                    DeprecatedEvent event = DeprecatedEvent.builder()
                        .eventId(uuidConverter.convertEntity(eventId))
                        .userId(uuidConverter.convertEntity(userId))
                        .startDate(localDateEncryptor.decrypt(rs.getString("start_date"), userId, eventId, "start-date"))
                        .time(localTimeEncryptor.decrypt(rs.getString("time"), userId, eventId, "time"))
                        .repetitionType(RepetitionType.valueOf(rs.getString("repetition_type")))
                        .repetitionData(stringEncryptor.decrypt(rs.getString("repetition_data"), userId, eventId, "repetition-data"))
                        .title(stringEncryptor.decrypt(rs.getString("title"), userId, eventId, "title"))
                        .content(stringEncryptor.decrypt(rs.getString("content"), userId, eventId, "content"))
                        .repeat(Optional.ofNullable(integerEncryptor.decrypt(rs.getString("repeat"), userId, eventId, "repeat")).orElse(1))
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
                    String occurrenceId = rs.getString("occurrence_id");
                    String userId = rs.getString("user_id");

                    String status = rs.getString("status");
                    DeprecatedOccurrence occurrence = DeprecatedOccurrence.builder()
                        .occurrenceId(uuidConverter.convertEntity(occurrenceId))
                        .eventId(uuidConverter.convertEntity(rs.getString("event_id")))
                        .userId(uuidConverter.convertEntity(userId))
                        .date(localDateEncryptor.decrypt(rs.getString("date"), userId, occurrenceId, "date"))
                        .time(localTimeEncryptor.decrypt(rs.getString("time"), userId, occurrenceId, "time"))
                        .status("VIRTUAL".equals(status) ? OccurrenceStatus.PENDING : OccurrenceStatus.valueOf(status))
                        .note(stringEncryptor.decrypt(rs.getString("note"), userId, occurrenceId, "note"))
                        .build();
                    occurrences.add(occurrence);
                }
                return occurrences;
            }
        );
    }
}
