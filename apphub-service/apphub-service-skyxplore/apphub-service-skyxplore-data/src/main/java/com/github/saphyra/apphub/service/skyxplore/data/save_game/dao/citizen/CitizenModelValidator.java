package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.citizen;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class CitizenModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(CitizenModel citizenModel) {
        gameItemValidator.validate(citizenModel);

        if (isNull(citizenModel.getLocation())) {
            throw ExceptionFactory.invalidParam("location", "must not be null");
        }

        if (isNull(citizenModel.getName())) {
            throw ExceptionFactory.invalidParam("name", "must not be null");
        }

        if (isNull(citizenModel.getMorale())) {
            throw ExceptionFactory.invalidParam("morale", "must not be null");
        }

        if (isNull(citizenModel.getSatiety())) {
            throw ExceptionFactory.invalidParam("satiety", "must not be null");
        }
    }
}
