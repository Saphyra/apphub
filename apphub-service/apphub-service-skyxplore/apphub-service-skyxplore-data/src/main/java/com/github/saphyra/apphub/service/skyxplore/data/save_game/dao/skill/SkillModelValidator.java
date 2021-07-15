package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.skill;

import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class SkillModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(SkillModel model) {
        gameItemValidator.validate(model);

        if (isNull(model.getCitizenId())) {
            throw ExceptionFactory.invalidParam("citizenId", "must not be null");
        }

        if (isNull(model.getSkillType())) {
            throw ExceptionFactory.invalidParam("skillType", "must not be null");
        }

        if (isNull(model.getLevel())) {
            throw ExceptionFactory.invalidParam("level", "must not be null");
        }

        if (isNull(model.getExperience())) {
            throw ExceptionFactory.invalidParam("experience", "must not be null");
        }

        if (isNull(model.getNextLevel())) {
            throw ExceptionFactory.invalidParam("nextLevel", "must not be null");
        }
    }
}
