package com.github.saphyra.apphub.service.diary.dao.event;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
class EventConverter extends ConverterBase<EventEntity, Event> {
    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;

    @Override
    protected EventEntity processDomainConversion(Event domain) {
        String userId = uuidConverter.convertDomain(domain.getUserId());
        return EventEntity.builder()
            .eventId(uuidConverter.convertDomain(domain.getEventId()))
            .userId(userId)
            .startDate(stringEncryptor.encryptEntity(domain.getStartDate().toString(), userId))
            .repetitionType(domain.getRepetitionType())
            .repetitionData(stringEncryptor.encryptEntity(domain.getRepetitionData(), userId))
            .title(stringEncryptor.encryptEntity(domain.getTitle(), userId))
            .content(stringEncryptor.encryptEntity(domain.getContent(), userId))
            .build();
    }

    @Override
    protected Event processEntityConversion(EventEntity entity) {
        return Event.builder()
            .eventId(uuidConverter.convertEntity(entity.getEventId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .startDate(LocalDate.parse(stringEncryptor.decryptEntity(entity.getStartDate(), entity.getUserId())))
            .repetitionType(entity.getRepetitionType())
            .repetitionData(stringEncryptor.decryptEntity(entity.getRepetitionData(), entity.getUserId()))
            .title(stringEncryptor.decryptEntity(entity.getTitle(), entity.getUserId()))
            .content(stringEncryptor.decryptEntity(entity.getContent(), entity.getUserId()))
            .build();
    }
}
