package com.github.saphyra.apphub.service.custom.villany_atesz.cart.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AddToCartRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.CartDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class AddToCartRequestValidator {
    private final CartDao cartDao;
    private final StockItemDao stockItemDao;

    void validate(AddToCartRequest request) {
        ValidationUtil.notNull(request.getCartId(), "cartId");
        ValidationUtil.notNull(request.getStockItemId(), "stockItemId");
        ValidationUtil.notZero(request.getAmount(), "amount");

        cartDao.findByIdValidated(request.getCartId());
        stockItemDao.findByIdValidated(request.getStockItemId());
    }
}
