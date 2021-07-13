package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class CoordinateLoader {
    private final GameItemLoader gameItemLoader;

    CoordinateModel loadById(UUID coordinateId) {
        Optional<CoordinateModel> coordinateModel = gameItemLoader.loadItem(coordinateId, GameItemType.COORDINATE);
        return coordinateModel.orElse(null);
    }

    CoordinateModel loadOneByReferenceId(UUID referenceId) {
        return loadByReferenceId(referenceId)
            .stream()
            .findFirst()
            .orElse(null);
    }

    List<CoordinateModel> loadByReferenceId(UUID referenceId) {
        return gameItemLoader.loadChildren(referenceId, GameItemType.COORDINATE, CoordinateModel[].class);
    }
}
