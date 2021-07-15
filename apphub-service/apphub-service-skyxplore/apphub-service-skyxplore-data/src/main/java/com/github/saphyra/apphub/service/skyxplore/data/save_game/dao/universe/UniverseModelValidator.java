package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.universe;

import com.github.saphyra.apphub.api.skyxplore.model.game.UniverseModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class UniverseModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(UniverseModel model) {
        gameItemValidator.validateWithoutId(model);

        if (isNull(model.getSize())) {
            throw ExceptionFactory.invalidParam("size", "must not be null");
        }
    }
}
