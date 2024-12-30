package com.github.saphyra.apphub.service.elite_base.dao.body;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class BodyFactory {
    private final IdGenerator idGenerator;

    public Body create(LocalDateTime timestamp, UUID starSystemId, BodyType bodyType, Long bodyId, String bodyName) {
        return Body.builder()
            .id(idGenerator.randomUuid())
            .lastUpdate(timestamp)
            .starSystemId(starSystemId)
            .type(bodyType)
            .bodyId(bodyId)
            .bodyName(bodyName)
            .build();
    }
}
