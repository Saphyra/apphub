package com.github.saphyra.apphub.service.diary.dao.event;

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
    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;
    private final IntegerEncryptor integerEncryptor;
    private final AccessTokenProvider accessTokenProvider;

    @Override
    protected EventEntity processDomainConversion(Event domain) {
        String userId = accessTokenProvider.getUidAsString();
        return EventEntity.builder()
            .eventId(uuidConverter.convertDomain(domain.getEventId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .startDate(stringEncryptor.encryptEntity(domain.getStartDate().toString(), userId))
            .time(stringEncryptor.encryptEntity(Optional.ofNullable(domain.getTime()).map(Objects::toString).orElse(null), userId))
            .repetitionType(domain.getRepetitionType())
            .repetitionData(stringEncryptor.encryptEntity(domain.getRepetitionData(), userId))
            .title(stringEncryptor.encryptEntity(domain.getTitle(), userId))
            .content(stringEncryptor.encryptEntity(domain.getContent(), userId))
            .repeat(integerEncryptor.encryptEntity(domain.getRepeat(), userId))
            .build();
    }

    @Override
    protected Event processEntityConversion(EventEntity entity) {
        String userId = accessTokenProvider.getUidAsString();
        return Event.builder()
            .eventId(uuidConverter.convertEntity(entity.getEventId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .startDate(LocalDate.parse(stringEncryptor.decryptEntity(entity.getStartDate(), userId)))
            .time(Optional.ofNullable(stringEncryptor.decryptEntity(entity.getTime(), userId)).map(LocalTime::parse).orElse(null))
            .repetitionType(entity.getRepetitionType())
            .repetitionData(stringEncryptor.decryptEntity(entity.getRepetitionData(), userId))
            .title(stringEncryptor.decryptEntity(entity.getTitle(), userId))
            .content(stringEncryptor.decryptEntity(entity.getContent(), userId))
            .repeat(Optional.ofNullable(entity.getRepeat()).map(repeat -> integerEncryptor.decryptEntity(repeat, userId)).orElse(1))
            .build();
    }
}
