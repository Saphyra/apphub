package com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReferredCoordinateFactory {
    private final IdGenerator idGenerator;
    private final CoordinateConverter coordinateConverter;

    public ReferredCoordinate save(GameProgressDiff progressDiff, GameData gameData, UUID referenceId, Coordinate coordinate, Integer order) {
        ReferredCoordinate referredCoordinate = create(referenceId, coordinate, order);

        gameData.getCoordinates()
            .add(referredCoordinate);

        progressDiff.save(coordinateConverter.convert(gameData.getGameId(), referredCoordinate));

        return referredCoordinate;
    }

    public ReferredCoordinate create(UUID referenceId, Coordinate coordinate) {
        return create(referenceId, coordinate, null);
    }

    public ReferredCoordinate create(UUID referenceId, Coordinate coordinate, Integer order) {
        return ReferredCoordinate.builder()
            .referredCoordinateId(idGenerator.randomUuid())
            .referenceId(referenceId)
            .coordinate(coordinate)
            .order(order)
            .build();
    }
}
