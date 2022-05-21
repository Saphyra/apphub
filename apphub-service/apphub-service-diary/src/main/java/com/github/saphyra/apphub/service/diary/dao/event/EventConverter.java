package com.github.saphyra.apphub.service.diary.dao.event;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.StringStringMap;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class EventConverter extends ConverterBase<EventEntity, Event> {
    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;
    private final ObjectMapperWrapper objectMapperWrapper;

    @Override
    protected EventEntity processDomainConversion(Event domain) {
        String userId = uuidConverter.convertDomain(domain.getUserId());
        return EventEntity.builder()
            .eventId(uuidConverter.convertDomain(domain.getEventId()))
            .userId(userId)
            .repetitionType(domain.getRepetitionType())
            .repetitionData(objectMapperWrapper.writeValueAsPrettyString(domain.getRepetitionData()))
            .title(stringEncryptor.encryptEntity(domain.getTitle(), userId))
            .content(stringEncryptor.encryptEntity(domain.getContent(), userId))
            .build();
    }

    @Override
    protected Event processEntityConversion(EventEntity entity) {
        return Event.builder()
            .eventId(uuidConverter.convertEntity(entity.getEventId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .repetitionType(entity.getRepetitionType())
            .repetitionData(objectMapperWrapper.readValue(entity.getRepetitionData(), StringStringMap.class))
            .title(stringEncryptor.decryptEntity(entity.getTitle(), entity.getUserId()))
            .content(stringEncryptor.decryptEntity(entity.getContent(), entity.getUserId()))
            .build();
    }
}
