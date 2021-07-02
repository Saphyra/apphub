package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.surface;

import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class SurfaceModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(SurfaceModel model) {
        gameItemValidator.validate(model);

        if (isNull(model.getPlanetId())) {
            throw ExceptionFactory.invalidParam("planetId", "must not be null");
        }

        if (isNull(model.getCoordinate())) {
            throw ExceptionFactory.invalidParam("coordinate", "must not be null");
        }

        if (isNull(model.getSurfaceType())) {
            throw ExceptionFactory.invalidParam("surfaceType", "must not be null");
        }
    }
}
