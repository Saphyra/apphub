package com.github.saphyra.apphub.service.notebook.service.pin.group;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.notebook.dao.pin.mapping.PinMapping;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class PinMappingFactory {
    private final IdGenerator idGenerator;

    PinMapping create(UUID userId, UUID pinGroupId, UUID listItemId) {
        return PinMapping.builder()
            .pinMappingId(idGenerator.randomUuid())
            .userId(userId)
            .pinGroupId(pinGroupId)
            .listItemId(listItemId)
            .build();
    }
}
