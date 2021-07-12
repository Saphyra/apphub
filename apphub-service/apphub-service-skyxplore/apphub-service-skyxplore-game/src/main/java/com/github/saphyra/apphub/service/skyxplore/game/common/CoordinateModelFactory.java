package com.github.saphyra.apphub.service.skyxplore.game.common;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CoordinateModelFactory {
    private final IdGenerator idGenerator;

    public CoordinateModel create(Coordinate coordinate, UUID gameId, UUID referenceId) {
        CoordinateModel model = new CoordinateModel();
        model.setId(idGenerator.randomUuid());
        model.setGameId(gameId);
        model.setType(GameItemType.COORDINATE);
        model.setReferenceId(referenceId);
        model.setCoordinate(coordinate);
        return model;
    }
}
