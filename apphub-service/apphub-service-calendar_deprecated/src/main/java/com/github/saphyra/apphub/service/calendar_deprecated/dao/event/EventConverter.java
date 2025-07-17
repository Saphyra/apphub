package com.github.saphyra.apphub.service.calendar_deprecated.dao.event;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
class EventConverter extends ConverterBase<EventEntity, Event> {
    static final String COLUMN_START_DATE = "start-date";
    static final String COLUMN_TIME = "time";
    static final String COLUMN_REPETITION_DATA = "repetition-data";
    static final String COLUMN_TITLE = "title";
    static final String COLUMN_CONTENT = "content";
    static final String COLUMN_REPEAT = "repeat";

    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;
    private final IntegerEncryptor integerEncryptor;
    private final AccessTokenProvider accessTokenProvider;

    @Override
    protected EventEntity processDomainConversion(Event domain) {
        String userId = accessTokenProvider.getUserIdAsString();
        String eventId = uuidConverter.convertDomain(domain.getEventId());
        return EventEntity.builder()
            .eventId(eventId)
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .startDate(stringEncryptor.encrypt(domain.getStartDate().toString(), userId, eventId, COLUMN_START_DATE))
            .time(stringEncryptor.encrypt(Optional.ofNullable(domain.getTime()).map(Objects::toString).orElse(null), userId, eventId, COLUMN_TIME))
            .repetitionType(domain.getRepetitionType())
            .repetitionData(stringEncryptor.encrypt(domain.getRepetitionData(), userId, eventId, COLUMN_REPETITION_DATA))
            .title(stringEncryptor.encrypt(domain.getTitle(), userId, eventId, COLUMN_TITLE))
            .content(stringEncryptor.encrypt(domain.getContent(), userId, eventId, COLUMN_CONTENT))
            .repeat(integerEncryptor.encrypt(domain.getRepeat(), userId, eventId, COLUMN_REPEAT))
            .build();
    }

    @Override
    protected Event processEntityConversion(EventEntity entity) {
        String userId = accessTokenProvider.getUserIdAsString();
        return Event.builder()
            .eventId(uuidConverter.convertEntity(entity.getEventId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .startDate(LocalDate.parse(stringEncryptor.decrypt(entity.getStartDate(), userId, entity.getEventId(), COLUMN_START_DATE)))
            .time(Optional.ofNullable(stringEncryptor.decrypt(entity.getTime(), userId, entity.getEventId(), COLUMN_TIME)).map(LocalTime::parse).orElse(null))
            .repetitionType(entity.getRepetitionType())
            .repetitionData(stringEncryptor.decrypt(entity.getRepetitionData(), userId, entity.getEventId(), COLUMN_REPETITION_DATA))
            .title(stringEncryptor.decrypt(entity.getTitle(), userId, entity.getEventId(), COLUMN_TITLE))
            .content(stringEncryptor.decrypt(entity.getContent(), userId, entity.getEventId(), COLUMN_CONTENT))
            .repeat(Optional.ofNullable(integerEncryptor.decrypt(entity.getRepeat(), userId, entity.getEventId(), COLUMN_REPEAT)).orElseGet(() -> {
                log.info("OrElse");
                return 1;
            }))
            .build();
    }
}
