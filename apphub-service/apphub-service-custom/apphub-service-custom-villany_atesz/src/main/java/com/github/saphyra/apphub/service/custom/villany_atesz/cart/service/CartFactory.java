package com.github.saphyra.apphub.service.custom.villany_atesz.cart.service;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class CartFactory {
    private final IdGenerator idGenerator;
    private final DateTimeUtil dateTimeUtil;

    public Cart create(UUID userId, UUID contactId) {
        return Cart.builder()
            .cartId(idGenerator.randomUuid())
            .userId(userId)
            .contactId(contactId)
            .createdAt(dateTimeUtil.getCurrentDate())
            .finalized(false)
            .build();
    }
}
