package com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
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
class OccurrenceConverter extends ConverterBase<OccurrenceEntity, Occurrence> {
    static final String COLUMN_DATE = "date";
    static final String COLUMN_TIME = "time";
    static final String COLUMN_NOTE = "note";
    static final String COLUMN_TYPE = "type";

    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;
    private final AccessTokenProvider accessTokenProvider;

    @Override
    protected OccurrenceEntity processDomainConversion(Occurrence domain) {
        String userId = accessTokenProvider.getUserIdAsString();
        String occurrenceId = uuidConverter.convertDomain(domain.getOccurrenceId());
        return OccurrenceEntity.builder()
            .occurrenceId(occurrenceId)
            .eventId(uuidConverter.convertDomain(domain.getEventId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .date(stringEncryptor.encrypt(domain.getDate().toString(), userId, occurrenceId, COLUMN_DATE))
            .time(stringEncryptor.encrypt(Optional.ofNullable(domain.getTime()).map(Objects::toString).orElse(null), userId, occurrenceId, COLUMN_TIME))
            .status(domain.getStatus())
            .note(stringEncryptor.encrypt(domain.getNote(), userId, occurrenceId, COLUMN_NOTE))
            .type(stringEncryptor.encrypt(domain.getType().name(), userId, occurrenceId, COLUMN_TYPE))
            .build();
    }

    @Override
    protected Occurrence processEntityConversion(OccurrenceEntity entity) {
        String userId = accessTokenProvider.getUserIdAsString();
        return Occurrence.builder()
            .occurrenceId(uuidConverter.convertEntity(entity.getOccurrenceId()))
            .eventId(uuidConverter.convertEntity(entity.getEventId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .date(LocalDate.parse(stringEncryptor.decrypt(entity.getDate(), userId, entity.getOccurrenceId(), COLUMN_DATE)))
            .time(Optional.ofNullable(stringEncryptor.decrypt(entity.getTime(), userId, entity.getOccurrenceId(), COLUMN_TIME)).map(LocalTime::parse).orElse(null))
            .status(entity.getStatus())
            .note(stringEncryptor.decrypt(entity.getNote(), userId, entity.getOccurrenceId(), COLUMN_NOTE))
            .type(Optional.ofNullable(stringEncryptor.decrypt(entity.getType(), userId, entity.getOccurrenceId(), COLUMN_TYPE)).map(OccurrenceType::valueOf).orElse(OccurrenceType.DEFAULT))
            .build();
    }
}
