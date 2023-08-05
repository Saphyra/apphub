package com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.ReferredCoordinate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReferredCoordinateFactory {
    private final IdGenerator idGenerator;

    public ReferredCoordinate create(UUID referenceId, Coordinate coordinate) {
        return ReferredCoordinate.builder()
            .referredCoordinateId(idGenerator.randomUuid())
            .referenceId(referenceId)
            .coordinate(coordinate)
            .build();
    }
}
