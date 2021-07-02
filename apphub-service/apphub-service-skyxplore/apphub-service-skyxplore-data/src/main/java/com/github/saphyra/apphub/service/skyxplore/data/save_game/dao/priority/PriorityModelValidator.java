package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.priority;

import com.github.saphyra.apphub.api.skyxplore.model.game.PriorityModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriorityModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(PriorityModel model) {
        gameItemValidator.validateWithoutId(model);

        if (isNull(model.getLocation())) {
            throw ExceptionFactory.invalidParam("location", "must not be null");
        }
        if (isNull(model.getLocationType())) {
            throw ExceptionFactory.invalidParam("locationType", "must not be null");
        }

        if (isNull(model.getPriorityType())) {
            throw ExceptionFactory.invalidParam("priorityType", "must not be null");
        }

        if (isNull(model.getValue())) {
            throw ExceptionFactory.invalidParam("value", "must not be null");
        }
    }
}
