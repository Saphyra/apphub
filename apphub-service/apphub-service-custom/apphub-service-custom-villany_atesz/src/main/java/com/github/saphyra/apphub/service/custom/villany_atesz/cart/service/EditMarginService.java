package com.github.saphyra.apphub.service.custom.villany_atesz.cart.service;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.Cart;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.CartDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EditMarginService {
    private final CartDao cartDao;

    public void editMargin(UUID cartId, Double value) {
        ValidationUtil.atLeast(value, 0, "margin");

        Cart cart = cartDao.findByIdValidated(cartId);
        cart.setMargin(value);

        cartDao.save(cart);
    }
}
