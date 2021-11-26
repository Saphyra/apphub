package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.durability_item;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityItemModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class DurabilityItemValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(DurabilityItemModel durabilityItemModel) {
        gameItemValidator.validate(durabilityItemModel);

        if (isNull(durabilityItemModel.getParent())) {
            throw ExceptionFactory.invalidParam("parent", "must not be null");
        }

        if (isNull(durabilityItemModel.getMaxDurability())) {
            throw ExceptionFactory.invalidParam("maxDurability", "must not be null");
        }

        if (isNull(durabilityItemModel.getCurrentDurability())) {
            throw ExceptionFactory.invalidParam("currentDurability", "must not be null");
        }
    }
}
