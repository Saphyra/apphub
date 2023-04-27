package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuildingModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(BuildingModel model) {
        gameItemValidator.validate(model);

        if (isNull(model.getSurfaceId())) {
            throw ExceptionFactory.invalidParam("surfaceId", "must not be null");
        }

        if (isNull(model.getLocation())) {
            throw ExceptionFactory.invalidParam("location", "must not be null");
        }

        if (isNull(model.getDataId())) {
            throw ExceptionFactory.invalidParam("dataId", "must not be null");
        }

        if (isNull(model.getLevel())) {
            throw ExceptionFactory.invalidParam("level", "must not be null");
        }
    }
}
