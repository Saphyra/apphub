package com.github.saphyra.apphub.service.custom.villany_atesz.cart;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AddToCartRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CartDeletedResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CartModifiedResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CartResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CartView;
import com.github.saphyra.apphub.api.custom.villany_atesz.server.CartController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.service.AddToCartService;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.service.CartQueryService;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.service.CreateCartService;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.service.DeleteCartService;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.service.EditMarginService;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.service.FinalizeCartService;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.service.RemoveFromCartService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item.StockItemQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
class CartControllerImpl implements CartController {
    private final CreateCartService createCartService;
    private final CartQueryService cartQueryService;
    private final AddToCartService addToCartService;
    private final StockItemQueryService stockItemQueryService;
    private final FinalizeCartService finalizeCartService;
    private final DeleteCartService deleteCartService;
    private final RemoveFromCartService removeFromCartService;
    private final EditMarginService editMarginService;

    @Override
    public UUID createCart(OneParamRequest<UUID> contactId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a cart for contact {}", accessTokenHeader.getUserId(), contactId.getValue());

        return createCartService.create(accessTokenHeader.getUserId(), contactId.getValue());
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

    @Override
    public CartModifiedResponse addToCart(AddToCartRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to add item {} to cart {}", accessTokenHeader.getUserId(), request.getStockItemId(), request.getCartId());

        addToCartService.addToCart(accessTokenHeader.getUserId(), request);

        return CartModifiedResponse.builder()
            .cart(getCart(request.getCartId(), accessTokenHeader))
            .items(stockItemQueryService.getStockItems(accessTokenHeader.getUserId()))
            .build();
    }

    @Override
    public CartModifiedResponse removeFromCart(UUID cartId, UUID stockItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to remove cartItem {} from the cart.", accessTokenHeader.getUserId(), stockItemId);

        removeFromCartService.removeFromCart(cartId, stockItemId);

        return CartModifiedResponse.builder()
            .cart(getCart(cartId, accessTokenHeader))
            .items(stockItemQueryService.getStockItems(accessTokenHeader.getUserId()))
            .build();
    }

    @Override
    public CartDeletedResponse finalizeCart(UUID cartId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to finalize cart {}", accessTokenHeader.getUserId(), cartId);

        finalizeCartService.finalizeCart(cartId);

        return CartDeletedResponse.builder()
            .carts(getCarts(accessTokenHeader))
            .items(stockItemQueryService.getStockItems(accessTokenHeader.getUserId()))
            .build();
    }

    @Override
    public CartDeletedResponse deleteCart(UUID cartId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete cart {}", accessTokenHeader.getUserId(), cartId);

        deleteCartService.delete(accessTokenHeader.getUserId(), cartId);

        return CartDeletedResponse.builder()
            .carts(getCarts(accessTokenHeader))
            .items(stockItemQueryService.getStockItems(accessTokenHeader.getUserId()))
            .build();
    }

    @Override
    public void editMargin(OneParamRequest<Double> margin, UUID cartId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit margin of cart {}", accessTokenHeader.getUserId(), cartId);

        editMarginService.editMargin(cartId, margin.getValue());
    }
}
