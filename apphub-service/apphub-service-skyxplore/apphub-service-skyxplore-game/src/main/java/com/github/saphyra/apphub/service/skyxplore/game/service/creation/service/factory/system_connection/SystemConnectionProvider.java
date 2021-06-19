package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system_connection;

import com.github.saphyra.apphub.lib.common_util.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.geometry.Line;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SystemConnection;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@Builder
public class SystemConnectionProvider {
    private final ExecutorServiceBean executorServiceBean;
    private final AllSystemsConnectionProvider allSystemsConnectionProvider;
    private final CrossRemovalService crossRemovalService;
    private final TooShortConnectionRemovalService tooShortConnectionRemovalService;
    private final ConnectionOverflowRemovalService connectionOverflowRemovalService;
    private final LonelySystemConnectionService lonelySystemConnectionService;
    private final SystemConnectionFactory systemConnectionFactory;

    public List<SystemConnection> getConnections(Collection<Coordinate> systems) {
        log.info("Generating connections...");
        List<Line> lines = executorServiceBean.processCollectionWithWait(systems, i -> allSystemsConnectionProvider.connectToAllSystems(i, systems))
            .stream()
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
        log.info("Number of connections after connection all stars to each other: {}", lines.size());

        log.info("Removing crosses...");
        List<Line> withoutCrosses = crossRemovalService.removeCrosses(lines);
        log.info("Number of connections after removing crosses: {}", withoutCrosses.size());

        log.info("Removing connections too close to a system...");
        List<Line> withoutTooClose = tooShortConnectionRemovalService.filterLinesTooCloseToASystem(systems, withoutCrosses);
        log.info("Number of connections after removing the ones too close to a system: {}", withoutTooClose.size());

        log.info("Removing connections from systems with too much connections...");
        List<Line> limited = connectionOverflowRemovalService.removeConnectionOverflow(systems, withoutTooClose);
        log.info("Number of connections remaining: {}", limited.size());

        List<Line> allConnected = lonelySystemConnectionService.connectLonelySystems(systems, limited);

        return allConnected.stream()
            .peek(line -> log.debug("Connection: {}", line))
            .map(systemConnectionFactory::create)
            .collect(Collectors.toList());
    }
}
