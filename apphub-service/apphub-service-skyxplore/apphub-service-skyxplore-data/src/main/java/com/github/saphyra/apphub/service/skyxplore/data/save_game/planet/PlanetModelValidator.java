package com.github.saphyra.apphub.service.skyxplore.data.save_game.planet;

import com.github.saphyra.apphub.api.skyxplore.model.game.PlanetModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.GameItemValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlanetModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(PlanetModel model) {
        gameItemValidator.validate(model);

        if (isNull(model.getSolarSystemId())) {
            throw ExceptionFactory.invalidParam("solarSystemId", "must not be null");
        }

        if (isNull(model.getDefaultName())) {
            throw ExceptionFactory.invalidParam("defaultName", "must not be null");
        }

        if (isNull(model.getCustomNames())) {
            throw ExceptionFactory.invalidParam("customNames", "must not be null");
        }

        if (model.getCustomNames().values().stream().anyMatch(Objects::isNull)) {
            throw ExceptionFactory.invalidParam("customNames", "must not contain null");
        }

        if (isNull(model.getCoordinate())) {
            throw ExceptionFactory.invalidParam("coordinate", "must not be null");
        }

        if (isNull(model.getSize())) {
            throw ExceptionFactory.invalidParam("size", "must not be null");
        }
    }
}
