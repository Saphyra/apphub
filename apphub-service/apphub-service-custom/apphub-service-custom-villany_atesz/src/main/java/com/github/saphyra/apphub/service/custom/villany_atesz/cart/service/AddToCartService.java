package com.github.saphyra.apphub.service.custom.villany_atesz.cart.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AddToCartRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItemDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class AddToCartService {
    private final AddToCartRequestValidator addToCartRequestValidator;
    private final CartItemFactory cartItemFactory;
    private final CartItemDao cartItemDao;

    public void addToCart(UUID userId, AddToCartRequest request) {
        addToCartRequestValidator.validate(request);

        CartItem cartItem = cartItemFactory.create(userId, request);
        cartItemDao.save(cartItem);
    }
}
