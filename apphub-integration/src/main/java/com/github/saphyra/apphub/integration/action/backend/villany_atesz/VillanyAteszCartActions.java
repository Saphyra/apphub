package com.github.saphyra.apphub.integration.action.backend.villany_atesz;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.VillanyAteszEndpoints;
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
    public static Response getCreateResponse(int serverPort, UUID accessTokenId, UUID contactId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(contactId))
            .put(UrlFactory.create(serverPort, VillanyAteszEndpoints.VILLANY_ATESZ_CREATE_CART));
    }

    public static UUID createCart(int serverPort, UUID accessTokenId, UUID contactId) {
        Response response = getCreateResponse(serverPort, accessTokenId, contactId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(UUID.class);
    }

    public static List<CartResponse> getCarts(int serverPort, UUID accessTokenId) {
        Response response = getCartsResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(CartResponse[].class));
    }

    public static Response getCartsResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, VillanyAteszEndpoints.VILLANY_ATESZ_GET_CARTS));
    }

    public static Response getAddToCartResponse(int serverPort, UUID accessTokenId, AddToCartRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .post(UrlFactory.create(serverPort, VillanyAteszEndpoints.VILLANY_ATESZ_ADD_TO_CART));
    }

    public static CartModifiedResponse addToCart(int serverPort, UUID accessTokenId, AddToCartRequest request) {
        Response response = getAddToCartResponse(serverPort, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(CartModifiedResponse.class);
    }

    public static CartModifiedResponse removeFromCart(int serverPort, UUID accessTokenId, UUID cartId, UUID stockItemId) {
        Response response = getRemoveFromCartResponse(serverPort, accessTokenId, cartId, stockItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(CartModifiedResponse.class);
    }

    public static Response getRemoveFromCartResponse(int serverPort, UUID accessTokenId, UUID cartId, UUID stockItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, VillanyAteszEndpoints.VILLANY_ATESZ_REMOVE_FROM_CART, Map.of("cartId", cartId, "stockItemId", stockItemId)));
    }

    public static CartDeletedResponse finalize(int serverPort, UUID accessTokenId, UUID cartId) {
        Response response = getFinalizeCartResponse(serverPort, accessTokenId, cartId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(CartDeletedResponse.class);
    }

    public static Response getFinalizeCartResponse(int serverPort, UUID accessTokenId, UUID cartId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(serverPort, VillanyAteszEndpoints.VILLANY_ATESZ_FINALIZE_CART, "cartId", cartId));
    }

    public static CartDeletedResponse delete(int serverPort, UUID accessTokenId, UUID cartId) {
        Response response = getDeleteResponse(serverPort, accessTokenId, cartId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(CartDeletedResponse.class);
    }

    public static Response getDeleteResponse(int serverPort, UUID accessTokenId, UUID cartId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, VillanyAteszEndpoints.VILLANY_ATESZ_DELETE_CART, "cartId", cartId));
    }

    public static CartView getCart(int serverPort, UUID accessTokenId, UUID cartId) {
        Response response = getCartResponse(serverPort, accessTokenId, cartId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(CartView.class);
    }

    public static Response getCartResponse(int serverPort, UUID accessTokenId, UUID cartId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, VillanyAteszEndpoints.VILLANY_ATESZ_GET_CART, "cartId", cartId));
    }

    public static Response getEditMarginResponse(int serverPort, UUID accessTokenId, UUID cartId, Double margin) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(margin))
            .post(UrlFactory.create(serverPort, VillanyAteszEndpoints.VILLANY_ATESZ_CART_EDIT_MARGIN, "cartId", cartId));
    }

    public static void editMargin(int serverPort, UUID accessTokenId, UUID cartId, Double margin) {
        Response response = getEditMarginResponse(serverPort, accessTokenId, cartId, margin);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
