package com.github.saphyra.apphub.service.diary.dao.occurance;

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
    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;
    private final AccessTokenProvider accessTokenProvider;

    @Override
    protected OccurrenceEntity processDomainConversion(Occurrence domain) {
        String userId = accessTokenProvider.getUserIdAsString();
        return OccurrenceEntity.builder()
            .occurrenceId(uuidConverter.convertDomain(domain.getOccurrenceId()))
            .eventId(uuidConverter.convertDomain(domain.getEventId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .date(stringEncryptor.encryptEntity(domain.getDate().toString(), userId))
            .time(stringEncryptor.encryptEntity(Optional.ofNullable(domain.getTime()).map(Objects::toString).orElse(null), userId))
            .status(domain.getStatus())
            .note(stringEncryptor.encryptEntity(domain.getNote(), userId))
            .type(stringEncryptor.encryptEntity(domain.getType().name(), userId))
            .build();
    }

    @Override
    protected Occurrence processEntityConversion(OccurrenceEntity entity) {
        String userId = accessTokenProvider.getUserIdAsString();
        return Occurrence.builder()
            .occurrenceId(uuidConverter.convertEntity(entity.getOccurrenceId()))
            .eventId(uuidConverter.convertEntity(entity.getEventId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .date(LocalDate.parse(stringEncryptor.decryptEntity(entity.getDate(), userId)))
            .time(Optional.ofNullable(stringEncryptor.decryptEntity(entity.getTime(), userId)).map(LocalTime::parse).orElse(null))
            .status(entity.getStatus())
            .note(stringEncryptor.decryptEntity(entity.getNote(), userId))
            .type(Optional.ofNullable(entity.getType()).map(type -> stringEncryptor.decryptEntity(type, userId)).map(OccurrenceType::valueOf).orElse(OccurrenceType.DEFAULT))
            .build();
    }
}
