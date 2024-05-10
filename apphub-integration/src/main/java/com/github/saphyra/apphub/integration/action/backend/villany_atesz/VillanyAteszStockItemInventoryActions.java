package com.github.saphyra.apphub.integration.action.backend.villany_atesz;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.StockItemInventoryResponse;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class VillanyAteszStockItemInventoryActions {
    public static Response getEditCategoryResponse(UUID accessTokenId, UUID stockItemId, UUID stockCategoryId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(stockCategoryId))
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_CATEGORY, "stockItemId", stockItemId));
    }

    public static void editCategory(UUID accessTokenId, UUID stockItemId, UUID stockCategoryId) {
        Response response = getEditCategoryResponse(accessTokenId, stockItemId, stockCategoryId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static List<StockItemInventoryResponse> getItems(UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_GET_ITEMS));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StockItemInventoryResponse[].class));
    }

    public static Response getEditInventoriedResponse(UUID accessTokenId, UUID stockItemId, Boolean inventoried) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(inventoried))
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_INVENTORIED, "stockItemId", stockItemId));
    }

    public static void editInventoried(UUID accessTokenId, UUID stockItemId, Boolean inventoried) {
        Response response = getEditInventoriedResponse(accessTokenId, stockItemId, inventoried);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditNameResponse(UUID accessTokenId, UUID stockItemId, String name) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(name))
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_NAME, "stockItemId", stockItemId));
    }

    public static void editName(UUID accessTokenId, UUID stockItemId, String name) {
        Response response = getEditNameResponse(accessTokenId, stockItemId, name);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditSerialNumberResponse(UUID accessTokenId, UUID stockItemId, String serialNumber) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(serialNumber))
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_SERIAL_NUMBER, "stockItemId", stockItemId));
    }

    public static void editSerialNumber(UUID accessTokenId, UUID stockItemId, String serialNumber) {
        Response response = getEditSerialNumberResponse(accessTokenId, stockItemId, serialNumber);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditInCarResponse(UUID accessTokenId, UUID stockItemId, Integer inCar) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(inCar))
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_IN_CAR, "stockItemId", stockItemId));

    }

    public static void editInCar(UUID accessTokenId, UUID stockItemId, Integer inCar) {
        Response response = getEditInCarResponse(accessTokenId, stockItemId, inCar);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditInStorageResponse(UUID accessTokenId, UUID stockItemId, Integer inStorage) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(inStorage))
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_IN_STORAGE, "stockItemId", stockItemId));
    }

    public static void editInStorage(UUID accessTokenId, UUID stockItemId, Integer inStorage) {
        Response response = getEditInStorageResponse(accessTokenId, stockItemId, inStorage);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
