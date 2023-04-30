package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.deconstruction;

import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class DeconstructionModelValidator {
    private final GameItemValidator gameItemValidator;

    void validate(DeconstructionModel model) {
        gameItemValidator.validate(model);

        ValidationUtil.notNull(model.getExternalReference(), "externalReference");
        ValidationUtil.notNull(model.getCurrentWorkPoints(), "currentWorkPoints");
        ValidationUtil.betweenInclusive(model.getPriority(), 1, 10, "priority");
    }
}
