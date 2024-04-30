package com.github.saphyra.apphub.service.custom.villany_atesz.cart;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CartResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CartView;
import com.github.saphyra.apphub.api.custom.villany_atesz.server.CartController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.service.CartQueryService;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.service.CreateCartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
//TODO unit test
class CartControllerImpl implements CartController {
    private final CreateCartService createCartService;
    private final CartQueryService cartQueryService;

    @Override
    public void createCart(OneParamRequest<UUID> contactId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a cart for contact {}", accessTokenHeader.getUserId(), contactId.getValue());

        createCartService.create(accessTokenHeader.getUserId(), contactId.getValue());
    }

    @Override
    public List<CartResponse> getCarts(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query their carts.", accessTokenHeader.getUserId());
        return cartQueryService.getCarts(accessTokenHeader.getUserId());
    }

    @Override
    public CartView getCart(UUID cartId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query cart {}", accessTokenHeader.getUserId(), cartId);

        return cartQueryService.getCart(cartId);
    }
}
