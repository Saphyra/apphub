package com.github.saphyra.apphub.service.custom.elite_base.dao.last_update;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LastUpdateFactory {
    public LastUpdate create(UUID externalReference, EntityTypeProvider type, LocalDateTime timestamp) {
        return LastUpdate.builder()
            .externalReference(externalReference)
            .type(type.get())
            .lastUpdate(timestamp)
            .build();
    }
}
