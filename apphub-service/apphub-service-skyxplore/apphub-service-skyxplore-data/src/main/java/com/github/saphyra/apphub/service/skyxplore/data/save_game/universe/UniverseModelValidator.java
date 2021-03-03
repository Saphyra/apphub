package com.github.saphyra.apphub.service.skyxplore.data.save_game.universe;

import com.github.saphyra.apphub.api.skyxplore.model.game.UniverseModel;
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
public class UniverseModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(UniverseModel model) {
        gameItemValidator.validateWithoutId(model);

        if (isNull(model.getSize())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "size", "must not be null"), "size must not be null.");
        }
    }
}
