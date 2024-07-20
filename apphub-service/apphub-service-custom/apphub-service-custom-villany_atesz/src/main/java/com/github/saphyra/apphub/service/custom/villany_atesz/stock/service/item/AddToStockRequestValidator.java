package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AddToStockRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class AddToStockRequestValidator {
    public void validate(List<AddToStockRequest> requests) {
        requests.forEach(this::validate);
    }

    private void validate(AddToStockRequest request) {
        ValidationUtil.notNull(request.getStockItemId(), "stockItemId");
        ValidationUtil.notNull(request.getInCar(), "inCar");
        ValidationUtil.notNull(request.getInStorage(), "inStorage");
        ValidationUtil.notNull(request.getPrice(), "price");
        ValidationUtil.notNull(request.getBarCode(), "barCode");
        ValidationUtil.notNull(request.getForceUpdatePrice(), "forceUpdatePrice");
    }
}
