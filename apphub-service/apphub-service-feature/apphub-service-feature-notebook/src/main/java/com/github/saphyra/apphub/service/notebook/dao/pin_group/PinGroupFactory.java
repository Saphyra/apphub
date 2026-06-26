package com.github.saphyra.apphub.service.notebook.dao.pin_group;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PinGroupFactory {
    private final IdGenerator idGenerator;
    private final DateTimeUtil dateTimeUtil;

    public PinGroup create(UUID userId, String pinGroupName) {
        return PinGroup.builder()
            .userId(userId)
            .pinGroupId(idGenerator.randomUuid())
            .pinGroupName(pinGroupName)
            .lastOpened(dateTimeUtil.getZeroLocalDateTime())
            .build();
    }
}
