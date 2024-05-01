package com.github.saphyra.apphub.service.custom.villany_atesz.cart.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AddToCartRequest;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
class CartItemFactory {
    private final IdGenerator idGenerator;

    CartItem create(UUID userId, AddToCartRequest request) {
        return CartItem.builder()
            .cartItemId(idGenerator.randomUuid())
            .userId(userId)
            .cartId(request.getCartId())
            .stockItemId(request.getStockItemId())
            .amount(request.getAmount())
            .build();
    }
}
