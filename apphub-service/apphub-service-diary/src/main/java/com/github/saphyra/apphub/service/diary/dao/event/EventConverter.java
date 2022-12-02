package com.github.saphyra.apphub.service.diary.dao.event;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
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

    @Override
    protected EventEntity processDomainConversion(Event domain) {
        String userId = uuidConverter.convertDomain(domain.getUserId());
        return EventEntity.builder()
            .eventId(uuidConverter.convertDomain(domain.getEventId()))
            .userId(userId)
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
        return Event.builder()
            .eventId(uuidConverter.convertEntity(entity.getEventId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .startDate(LocalDate.parse(stringEncryptor.decryptEntity(entity.getStartDate(), entity.getUserId())))
            .time(Optional.ofNullable(stringEncryptor.decryptEntity(entity.getTime(), entity.getUserId())).map(LocalTime::parse).orElse(null))
            .repetitionType(entity.getRepetitionType())
            .repetitionData(stringEncryptor.decryptEntity(entity.getRepetitionData(), entity.getUserId()))
            .title(stringEncryptor.decryptEntity(entity.getTitle(), entity.getUserId()))
            .content(stringEncryptor.decryptEntity(entity.getContent(), entity.getUserId()))
            .repeat(Optional.ofNullable(entity.getRepeat()).map(repeat -> integerEncryptor.decryptEntity(repeat, entity.getUserId())).orElse(1))
            .build();
    }
}
