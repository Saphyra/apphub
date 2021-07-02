package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.reserved_storage;

import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservedStorageModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(ReservedStorageModel model) {
        gameItemValidator.validate(model);

        if (isNull(model.getExternalReference())) {
            throw ExceptionFactory.invalidParam("externalReference", "must not be null");
        }

        if (isNull(model.getDataId())) {
            throw ExceptionFactory.invalidParam("dataId", "must not be null");
        }

        if (isNull(model.getAmount())) {
            throw ExceptionFactory.invalidParam("amount", "must not be null");
        }
    }
}
