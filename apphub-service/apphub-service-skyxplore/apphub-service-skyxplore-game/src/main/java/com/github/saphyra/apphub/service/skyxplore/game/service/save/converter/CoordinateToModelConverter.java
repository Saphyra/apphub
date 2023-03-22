package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.ReferredCoordinate;
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
//TODO unit test
public class CoordinateToModelConverter {
    public List<CoordinateModel> convert(UUID gameId, Collection<ReferredCoordinate> coordinates) {
        return coordinates.stream()
            .map(coordinate -> convert(gameId, coordinate))
            .collect(Collectors.toList());
    }

    public CoordinateModel convert(UUID gameId, ReferredCoordinate coordinate) {
        CoordinateModel model = new CoordinateModel();
        model.setId(coordinate.getReferenceId());
        model.setGameId(gameId);
        model.setType(GameItemType.COORDINATE);
        model.setCoordinate(coordinate.getCoordinate());

        return model;
    }
}
