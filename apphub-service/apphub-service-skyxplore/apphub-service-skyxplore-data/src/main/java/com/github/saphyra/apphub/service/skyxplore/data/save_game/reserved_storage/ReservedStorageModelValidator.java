package com.github.saphyra.apphub.service.skyxplore.data.save_game.reserved_storage;

import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
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
public class ReservedStorageModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(ReservedStorageModel model) {
        gameItemValidator.validate(model);

        if (isNull(model.getExternalReference())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "externalReference", "must not be null."), "externalReference must not be null.");
        }

        if (isNull(model.getDataId())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "dataId", "must not be null."), "dataId must not be null.");
        }

        if (isNull(model.getAmount())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "amount", "must not be null."), "amount must not be null.");
        }
    }
}
