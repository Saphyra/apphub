package com.github.saphyra.apphub.service.elite_base.message_processing.dao.last_update;

import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
//TODO unit test
class LastUpdateConverter extends ConverterBase<LastUpdateEntity, LastUpdate> {
    private final UuidConverter uuidConverter;
    private final DateTimeConverter dateTimeConverter;

    @Override
    protected LastUpdateEntity processDomainConversion(LastUpdate domain) {
        return LastUpdateEntity.builder()
            .id(LastUpdateId.builder()
                .externalReference(uuidConverter.convertDomain(domain.getExternalReference()))
                .type(domain.getType())
                .build())
            .lastUpdate(dateTimeConverter.convertDomain(domain.getLastUpdate()))
            .build();
    }

    @Override
    protected LastUpdate processEntityConversion(LastUpdateEntity entity) {
        return LastUpdate.builder()
            .externalReference(uuidConverter.convertEntity(entity.getId().getExternalReference()))
            .type(entity.getId().getType())
            .lastUpdate(dateTimeConverter.convertToLocalDateTime(entity.getLastUpdate()))
            .build();
    }
}
