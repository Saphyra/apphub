package com.github.saphyra.apphub.service.custom.villany_atesz.commission;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CommissionCartResponse;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.Cart;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.CartDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItemDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.service.CartQueryService;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao.ContactDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class CommissionCartQueryService {
    private final CartDao cartDao;
    private final ContactDao contactDao;
    private final CartItemDao cartItemDao;
    private final CartQueryService cartQueryService;

    public CommissionCartResponse getCart(UUID cartId) {
        Cart cart = cartDao.findByIdValidated(cartId);

        return CommissionCartResponse.builder()
            .cartId(cartId)
            .contactName(contactDao.findByIdValidated(cart.getContactId()).getName())
            .cartCost(calculateCartCost(cartId))
            .margin(cart.getMargin())
            .build();
    }

    private Integer calculateCartCost(UUID cartId) {
        return cartQueryService.getTotalPrice(cartItemDao.getByCartId(cartId));
    }
}
