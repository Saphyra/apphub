package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system_connection;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SystemConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class SystemConnectionFactory {
    private final IdGenerator idGenerator;

    SystemConnection create(Line line) {
        return SystemConnection.builder()
            .systemConnectionId(idGenerator.randomUuid())
            .line(line)
            .build();
    }
}
