package com.github.saphyra.apphub.service.diary.dao.occurance;

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
class OccurrenceConverter extends ConverterBase<OccurrenceEntity, Occurrence> {
    private final UuidConverter uuidConverter;
    private final StringEncryptor stringEncryptor;

    @Override
    protected OccurrenceEntity processDomainConversion(Occurrence domain) {
        String userId = uuidConverter.convertDomain(domain.getUserId());
        return OccurrenceEntity.builder()
            .occurrenceId(uuidConverter.convertDomain(domain.getOccurrenceId()))
            .eventId(uuidConverter.convertDomain(domain.getEventId()))
            .userId(userId)
            .date(domain.getDate())
            .status(domain.getStatus())
            .note(stringEncryptor.encryptEntity(domain.getNote(), userId))
            .build();
    }

    @Override
    protected Occurrence processEntityConversion(OccurrenceEntity entity) {
        return Occurrence.builder()
            .occurrenceId(uuidConverter.convertEntity(entity.getOccurrenceId()))
            .eventId(uuidConverter.convertEntity(entity.getEventId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .date(entity.getDate())
            .status(entity.getStatus())
            .note(stringEncryptor.decryptEntity(entity.getNote(), entity.getUserId()))
            .build();
    }
}
