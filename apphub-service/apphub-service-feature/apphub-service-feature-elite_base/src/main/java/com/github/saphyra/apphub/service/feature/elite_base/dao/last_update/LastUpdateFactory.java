package com.github.saphyra.apphub.service.feature.elite_base.dao.last_update;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LastUpdateFactory {
    private final DateTimeUtil dateTimeUtil;
    private final UuidConverter uuidConverter;

    public LastUpdate create(UUID externalReference, ObjectType type, LocalDateTime timestamp) {
        return create(uuidConverter.convertDomain(externalReference), type, timestamp);
    }

    public LastUpdate create(String externalReference, ObjectType type, LocalDateTime timestamp) {
        return LastUpdate.builder()
            .externalReference(externalReference)
            .type(type)
            .lastUpdate(timestamp)
            .build();
    }

    public LastUpdate create(String externalReference, ObjectType objectType) {
        return create(externalReference, objectType, dateTimeUtil.getZeroLocalDateTime());
    }
}
