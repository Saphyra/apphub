package com.github.saphyra.apphub.integration.action.backend.villany_atesz;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.AcquisitionRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.AddToStockRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CreateStockItemRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.StockItemOverviewResponse;
import io.restassured.response.Response;

import java.time.LocalDate;
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
        Response response = getStockItemsResponse(accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StockItemOverviewResponse[].class));
    }

    public static Response getStockItemsResponse(UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.VILLANY_ATESZ_GET_STOCK_ITEMS));
    }

    public static Response getAcquireResponse(UUID accessTokenId, LocalDate date, AddToStockRequest... requests) {
        return getAcquireResponse(accessTokenId, date, Arrays.asList(requests));
    }

    public static Response getAcquireResponse(UUID accessTokenId, LocalDate date, List<AddToStockRequest> requests) {
        AcquisitionRequest acquisitionRequest = AcquisitionRequest.builder()
            .items(requests)
            .acquiredAt(date)
            .build();

        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(acquisitionRequest)
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_STOCK_ACQUIRE));
    }

    public static void acquire(UUID accessTokenId, LocalDate date, AddToStockRequest... requests) {
        Response response = getAcquireResponse(accessTokenId, date, requests);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static List<StockItemOverviewResponse> delete(UUID accessTokenId, UUID stockItemId) {
        Response response = getDeleteResponse(accessTokenId, stockItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StockItemOverviewResponse[].class));
    }

    public static Response getDeleteResponse(UUID accessTokenId, UUID stockItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.VILLANY_ATESZ_DELETE_STOCK_ITEM, "stockItemId", stockItemId));
    }

    public static Response getStockItemsForCategoryResponse(UUID accessTokenId, UUID stockCategoryId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.VILLANY_ATESZ_GET_STOCK_ITEMS_FOR_CATEGORY, "stockCategoryId", stockCategoryId));
    }

    public static Response getFindByBarcodeResponse(UUID accessTokenId, String barCode) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(barCode))
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_FIND_STOCK_ITEM_BY_BAR_CODE));
    }

    public static Response getFindBarCodeByStockItemIdResponse(UUID accessTokenId, UUID stockItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.VILLANY_ATESZ_FIND_BAR_CODE_BY_STOCK_ITEM_ID, "stockItemId", stockItemId));
    }
}
