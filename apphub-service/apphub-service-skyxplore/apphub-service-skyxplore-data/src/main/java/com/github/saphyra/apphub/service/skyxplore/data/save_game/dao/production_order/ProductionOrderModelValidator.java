package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.production_order;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionOrderModel;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductionOrderModelValidator {
    private final GameItemValidator gameItemValidator;

    public void validate(ProductionOrderModel model) {
        gameItemValidator.validate(model);

        ValidationUtil.notNull(model.getLocation(), "location");
        ValidationUtil.notNull(model.getLocationType(), "locationType");
        ValidationUtil.notNull(model.getExternalReference(), "externalReference");
        ValidationUtil.notNull(model.getDataId(), "dataId");
        ValidationUtil.notNull(model.getAmount(), "amount");
        ValidationUtil.notNull(model.getRequiredWorkPoints(), "requiredWorkPoints");
        ValidationUtil.notNull(model.getCurrentWorkPoints(), "currentWorkPoints");
    }
}
