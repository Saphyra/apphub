package com.github.saphyra.apphub.integration.action.backend.villany_atesz;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.AddToStockRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CreateStockItemRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.StockItemOverviewResponse;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class VillanyAteszStockItemActions {
    public static Response getCreateResponse(UUID accessTokenId, CreateStockItemRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .put(UrlFactory.create(Endpoints.VILLANY_ATESZ_CREATE_STOCK_ITEM));
    }

    public static void create(UUID accessTokenId, CreateStockItemRequest request) {
        Response response = getCreateResponse(accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static List<StockItemOverviewResponse> getStockItems(UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.VILLANY_ATESZ_GET_STOCK_ITEMS));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StockItemOverviewResponse[].class));
    }

    public static Response getAcquireResponse(UUID accessTokenId, AddToStockRequest... requests) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(Arrays.asList(requests))
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_STOCK_ACQUIRE));
    }

    public static void acquire(UUID accessTokenId, AddToStockRequest... requests) {
        Response response = getAcquireResponse(accessTokenId, requests);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getMoveStockToCarResponse(UUID accessTokenId, UUID stockItemId, Integer amount) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(amount))
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_MOVE_STOCK_TO_CAR, "stockItemId", stockItemId));
    }

    public static List<StockItemOverviewResponse> moveStockToCar(UUID accessTokenId, UUID stockItemId, Integer amount) {
        Response response = getMoveStockToCarResponse(accessTokenId, stockItemId, amount);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StockItemOverviewResponse[].class));
    }

    public static Response getMoveStockToStorageResponse(UUID accessTokenId, UUID stockItemId, int amount) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(amount))
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_MOVE_STOCK_TO_STORAGE, "stockItemId", stockItemId));
    }

    public static List<StockItemOverviewResponse> moveStockToStorage(UUID accessTokenId, UUID stockItemId, Integer amount) {
        Response response = getMoveStockToStorageResponse(accessTokenId, stockItemId, amount);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StockItemOverviewResponse[].class));
    }

    public static List<StockItemOverviewResponse> delete(UUID accessTokenId, UUID stockItemId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.VILLANY_ATESZ_DELETE_STOCK_ITEM, "stockItemId", stockItemId));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StockItemOverviewResponse[].class));
    }
}
