package com.github.saphyra.apphub.api.custom.villany_atesz.server;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.AddToCartRequest;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CartDeletedResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CartModifiedResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CartResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CartView;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.VillanyAteszEndpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

public interface CartController {
    @PutMapping(VillanyAteszEndpoints.VILLANY_ATESZ_CREATE_CART)
    UUID createCart(@RequestBody OneParamRequest<UUID> contactId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(VillanyAteszEndpoints.VILLANY_ATESZ_GET_CARTS)
    List<CartResponse> getCarts(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(VillanyAteszEndpoints.VILLANY_ATESZ_GET_CART)
    CartView getCart(@PathVariable("cartId") UUID cartId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(VillanyAteszEndpoints.VILLANY_ATESZ_ADD_TO_CART)
    CartModifiedResponse addToCart(@RequestBody AddToCartRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(VillanyAteszEndpoints.VILLANY_ATESZ_REMOVE_FROM_CART)
    CartModifiedResponse removeFromCart(@PathVariable("cartId") UUID cartId, @PathVariable("stockItemId") UUID stockItemId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(VillanyAteszEndpoints.VILLANY_ATESZ_FINALIZE_CART)
    CartDeletedResponse finalizeCart(@PathVariable("cartId") UUID cartId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(VillanyAteszEndpoints.VILLANY_ATESZ_DELETE_CART)
    CartDeletedResponse deleteCart(@PathVariable("cartId") UUID cartId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(VillanyAteszEndpoints.VILLANY_ATESZ_CART_EDIT_MARGIN)
    void editMargin(@RequestBody OneParamRequest<Double> margin, @PathVariable("cartId") UUID cartId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
