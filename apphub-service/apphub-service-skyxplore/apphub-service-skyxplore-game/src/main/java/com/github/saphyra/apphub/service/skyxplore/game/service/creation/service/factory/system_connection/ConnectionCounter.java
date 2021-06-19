package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system_connection;

import java.util.List;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.Line;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
class ConnectionCounter {
    int getNumberOfConnections(Coordinate system, List<Line> lines) {
        int result = (int) lines.stream()
            .filter(line -> line.isEndpoint(system))
            .count();
        log.debug("{} has {} number of connections", system, result);
        return result;
    }
}
