package com.github.saphyra.apphub.service.skyxplore.game.service.map.query;

import com.github.saphyra.apphub.api.skyxplore.response.game.map.SolarSystemConnectionResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class SolarSystemConnectionResponseExtractor {
    List<SolarSystemConnectionResponse> getConnections(Universe universe) {
        return universe.getConnections()
            .stream()
            .map(connection -> SolarSystemConnectionResponse.builder()
                .a(connection.getLine().getLine().getA())
                .b(connection.getLine().getLine().getB())
                .build())
            .collect(Collectors.toList());
    }
}
