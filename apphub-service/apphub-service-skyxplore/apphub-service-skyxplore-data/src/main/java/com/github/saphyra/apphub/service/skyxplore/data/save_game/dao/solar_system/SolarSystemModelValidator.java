package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class SolarSystemModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(SolarSystemModel model) {
        gameItemValidator.validate(model);

        if (isNull(model.getRadius())) {
            throw ExceptionFactory.invalidParam("radius", "must not be null");
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
    }
}
