package com.github.saphyra.apphub.integration.action.backend.villany_atesz;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.AddToCartRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CartDeletedResponse;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CartModifiedResponse;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CartResponse;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CartView;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class VillanyAteszCartActions {
    public static Response getCreateResponse(UUID accessTokenId, UUID contactId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(contactId))
            .put(UrlFactory.create(Endpoints.VILLANY_ATESZ_CREATE_CART));
    }

    public static UUID create(UUID accessTokenId, UUID contactId) {
        Response response = getCreateResponse(accessTokenId, contactId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(UUID.class);
    }

    public static List<CartResponse> getCarts(UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.VILLANY_ATESZ_GET_CARTS));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(CartResponse[].class));
    }

    public static Response getAddToCartResponse(UUID accessTokenId, AddToCartRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_ADD_TO_CART));
    }

    public static CartModifiedResponse addToCart(UUID accessTokenId, AddToCartRequest request) {
        Response response = getAddToCartResponse(accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(CartModifiedResponse.class);
    }

    public static CartModifiedResponse removeFromCart(UUID accessTokenId, UUID cartId, UUID stockItemId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.VILLANY_ATESZ_REMOVE_FROM_CART, Map.of("cartId", cartId, "stockItemId", stockItemId)));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(CartModifiedResponse.class);
    }

    public static CartDeletedResponse finalize(UUID accessTokenId, UUID cartId) {
        Response response = getFinalizeCartResponse(accessTokenId, cartId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(CartDeletedResponse.class);
    }

    public static Response getFinalizeCartResponse(UUID accessTokenId, UUID cartId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_FINALIZE_CART, "cartId", cartId));
    }

    public static CartDeletedResponse delete(UUID accessTokenId, UUID cartId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.VILLANY_ATESZ_DELETE_CART, "cartId", cartId));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(CartDeletedResponse.class);
    }

    public static CartView getCart(UUID accessTokenId, UUID cartId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.VILLANY_ATESZ_GET_CART, "cartId", cartId));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(CartView.class);
    }
}
