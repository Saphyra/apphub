package com.github.saphyra.apphub.service.notebook.service.pin.group;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.notebook.dao.pin.group.PinGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class PinGroupFactory {
    private final IdGenerator idGenerator;

    PinGroup create(UUID userId, String pinGroupName) {
        return PinGroup.builder()
            .pinGroupId(idGenerator.randomUuid())
            .userId(userId)
            .pinGroupName(pinGroupName)
            .build();
    }
}
