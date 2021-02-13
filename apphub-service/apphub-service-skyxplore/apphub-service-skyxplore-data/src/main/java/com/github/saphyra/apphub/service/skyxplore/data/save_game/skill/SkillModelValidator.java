package com.github.saphyra.apphub.service.skyxplore.data.save_game.skill;

import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.GameItemValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class SkillModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(SkillModel model) {
        gameItemValidator.validate(model);

        if (isNull(model.getCitizenId())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "citizenId", "must not be null."), "citizenId must not be null.");
        }

        if (isNull(model.getSkillType())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "skillType", "must not be null."), "skillType must not be null.");
        }

        if (isNull(model.getLevel())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "level", "must not be null."), "level must not be null.");
        }

        if (isNull(model.getExperience())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "experience", "must not be null."), "experience must not be null.");
        }

        if (isNull(model.getNextLevel())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "nextLevel", "must not be null."), "nextLevel must not be null.");
        }
    }
}
