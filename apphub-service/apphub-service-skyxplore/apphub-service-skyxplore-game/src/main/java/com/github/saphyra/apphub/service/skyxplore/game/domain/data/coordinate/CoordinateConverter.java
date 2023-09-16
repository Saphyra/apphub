package com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameDataToModelConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CoordinateConverter implements GameDataToModelConverter {
    public List<CoordinateModel> convert(UUID gameId, Collection<ReferredCoordinate> coordinates) {
        return coordinates.stream()
            .map(coordinate -> convert(gameId, coordinate))
            .collect(Collectors.toList());
    }

    public CoordinateModel convert(UUID gameId, ReferredCoordinate coordinate) {
        CoordinateModel model = new CoordinateModel();
        model.setId(coordinate.getReferredCoordinateId());
        model.setReferenceId(coordinate.getReferenceId());
        model.setGameId(gameId);
        model.setType(GameItemType.COORDINATE);
        model.setCoordinate(coordinate.getCoordinate());

        return model;
    }

    @Override
    public List<CoordinateModel> convert(UUID gameId, GameData gameData) {
        return convert(gameId, gameData.getCoordinates());
    }
}
