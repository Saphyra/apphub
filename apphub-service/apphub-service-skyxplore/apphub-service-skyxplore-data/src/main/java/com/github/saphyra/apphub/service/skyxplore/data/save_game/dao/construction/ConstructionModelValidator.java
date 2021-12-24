package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ConstructionModelValidator {
    private final GameItemValidator gameItemValidator;

    void validate(ConstructionModel model) {
        gameItemValidator.validate(model);

        ValidationUtil.notNull(model.getLocation(), "location");
        ValidationUtil.notBlank(model.getLocationType(), "locationType");
        ValidationUtil.notNull(model.getRequiredWorkPoints(), "requiredWorkPoints");
        ValidationUtil.notNull(model.getCurrentWorkPoints(), "currentWorkPoints");
        ValidationUtil.notNull(model.getPriority(), "priority");
    }
}
