package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system_connection;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.game.common.LineModelWrapperFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SystemConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class SystemConnectionFactory {
    private final IdGenerator idGenerator;
    private final LineModelWrapperFactory lineModelWrapperFactory;

    SystemConnection create(UUID gameId, Line line) {
        UUID systemConnectionId = idGenerator.randomUuid();
        return SystemConnection.builder()
            .systemConnectionId(systemConnectionId)
            .line(lineModelWrapperFactory.create(line, gameId, systemConnectionId))
            .build();
    }
}
