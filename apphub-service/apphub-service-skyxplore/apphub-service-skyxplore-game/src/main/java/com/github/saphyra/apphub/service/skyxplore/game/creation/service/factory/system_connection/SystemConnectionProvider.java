package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system_connection;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.lib.common_util.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SystemConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class SystemConnectionProvider {
    private final ExecutorServiceBean executorServiceBean;
    private final IdGenerator idGenerator;

    private final AllSystemsConnectionProvider allSystemsConnectionProvider;
    private final CrossRemovalService crossRemovalService;
    private final TooShortConnectionRemovalService tooShortConnectionRemovalService;
    private final ConnectionOverflowRemovalService connectionOverflowRemovalService;

    public List<SystemConnection> create(Collection<Coordinate> systems) {
        log.info("Generating connections...");
        List<Line> lines = executorServiceBean.processCollectionWithWait(systems, i -> allSystemsConnectionProvider.connectToAllSystems(i, systems))
            .stream()
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
        log.info("Number of connections after connection all stars to each other: {}", lines.size());

        log.info("Removing crosses...");
        List<Line> withoutCrosses = crossRemovalService.removeCrosses(lines);
        log.info("Number of connections after removing crosses: {}", lines.size());

        log.info("Removing connections too close to a system...");
        List<Line> withoutTooClose = tooShortConnectionRemovalService.filterLinesTooCloseToASystem(systems, withoutCrosses);
        log.info("Number of connections after removing the ones too close to a system: {}", lines.size());

        log.info("Removing connections from systems with too much connections...");
        List<Line> limited = connectionOverflowRemovalService.removeConnectionOverflow(systems, withoutTooClose);
        log.info("Number of connections remaining: {}", lines.size());

        //TODO post process - If a star has no connection, connect it to the closest one
        return limited.stream()
            .peek(line -> log.debug("Connection: {}", line))
            .map(line -> SystemConnection.builder().systemConnectionId(idGenerator.randomUuid()).line(line).build())
            .collect(Collectors.toList());
    }
}
