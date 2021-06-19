package com.github.saphyra.apphub.service.skyxplore.data.save_game.stored_resource;

import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.GameItemValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class StoredResourceModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(StoredResourceModel model) {
        gameItemValidator.validate(model);

        if (isNull(model.getLocation())) {
            throw ExceptionFactory.invalidParam("location", "must not be null");
        }

        if (isNull(model.getLocationType())) {
            throw ExceptionFactory.invalidParam("locationType", "must not be null");
        }

        if (isNull(model.getDataId())) {
            throw ExceptionFactory.invalidParam("dataId", "must not be null");
        }

        if (isNull(model.getAmount())) {
            throw ExceptionFactory.invalidParam("amount", "must not be null");
        }
    }
}
