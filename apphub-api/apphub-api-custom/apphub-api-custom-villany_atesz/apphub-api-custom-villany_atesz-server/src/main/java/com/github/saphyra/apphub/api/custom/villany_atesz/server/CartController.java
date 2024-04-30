package com.github.saphyra.apphub.api.custom.villany_atesz.server;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CartResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CartView;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

//TODO api test
public interface CartController {
    @PutMapping(Endpoints.VILLANY_ATESZ_CREATE_CART)
    void createCart(@RequestBody OneParamRequest<UUID> contactId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER)AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.VILLANY_ATESZ_GET_CARTS)
    List<CartResponse> getCarts(@RequestHeader(Constants.ACCESS_TOKEN_HEADER)AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.VILLANY_ATESZ_GET_CART)
    CartView getCart(@PathVariable("cartId") UUID cartId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER)AccessTokenHeader accessTokenHeader);
}
