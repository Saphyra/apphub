package com.github.saphyra.apphub.service.skyxplore.data.save_game.alliance;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllianceModel;
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
public class AllianceModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(AllianceModel model) {
        gameItemValidator.validate(model);

        if (isNull(model.getName())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "name", "must not be null"), "name must not be null.");
        }
    }
}
