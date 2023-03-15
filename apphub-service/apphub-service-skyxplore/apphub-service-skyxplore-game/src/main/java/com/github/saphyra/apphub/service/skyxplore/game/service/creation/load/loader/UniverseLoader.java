package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.UniverseModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class UniverseLoader {
    private final GameItemLoader gameItemLoader;
    private final SystemConnectionLoader systemConnectionLoader;
    private final SolarSystemLoader solarSystemLoader;

    Universe load(UUID gameId) {
        Optional<UniverseModel> gameItemOptional = gameItemLoader.loadItem(gameId, GameItemType.UNIVERSE);
        UniverseModel universeModel = gameItemOptional
            .orElseThrow(() -> ExceptionFactory.reportedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Universe not found with id " + gameId));

        return Universe.builder()
            .size(universeModel.getSize())
            .systems(solarSystemLoader.load(gameId))
            .connections(systemConnectionLoader.load(gameId))
            .build();
    }
}
