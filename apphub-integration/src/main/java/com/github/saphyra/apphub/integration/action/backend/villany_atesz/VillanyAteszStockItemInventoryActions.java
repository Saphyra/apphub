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
    public static Response getEditCategoryResponse(int serverPort, UUID accessTokenId, UUID stockItemId, UUID stockCategoryId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(stockCategoryId))
            .post(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_CATEGORY, "stockItemId", stockItemId));
    }

    public static void editCategory(int serverPort, UUID accessTokenId, UUID stockItemId, UUID stockCategoryId) {
        Response response = getEditCategoryResponse(serverPort, accessTokenId, stockItemId, stockCategoryId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static List<StockItemInventoryResponse> getItems(int serverPort, UUID accessTokenId) {
        Response response = getItemsResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StockItemInventoryResponse[].class));
    }

    public static Response getItemsResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_GET_ITEMS));
    }

    public static Response getEditInventoriedResponse(int serverPort, UUID accessTokenId, UUID stockItemId, Boolean inventoried) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(inventoried))
            .post(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_INVENTORIED, "stockItemId", stockItemId));
    }

    public static void editInventoried(int serverPort, UUID accessTokenId, UUID stockItemId, Boolean inventoried) {
        Response response = getEditInventoriedResponse(serverPort, accessTokenId, stockItemId, inventoried);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditNameResponse(int serverPort, UUID accessTokenId, UUID stockItemId, String name) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(name))
            .post(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_NAME, "stockItemId", stockItemId));
    }

    public static void editName(int serverPort, UUID accessTokenId, UUID stockItemId, String name) {
        Response response = getEditNameResponse(serverPort, accessTokenId, stockItemId, name);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditSerialNumberResponse(int serverPort, UUID accessTokenId, UUID stockItemId, String serialNumber) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(serialNumber))
            .post(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_SERIAL_NUMBER, "stockItemId", stockItemId));
    }

    public static void editSerialNumber(int serverPort, UUID accessTokenId, UUID stockItemId, String serialNumber) {
        Response response = getEditSerialNumberResponse(serverPort, accessTokenId, stockItemId, serialNumber);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditInCarResponse(int serverPort, UUID accessTokenId, UUID stockItemId, Integer inCar) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(inCar))
            .post(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_IN_CAR, "stockItemId", stockItemId));

    }

    public static void editInCar(int serverPort, UUID accessTokenId, UUID stockItemId, Integer inCar) {
        Response response = getEditInCarResponse(serverPort, accessTokenId, stockItemId, inCar);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditInStorageResponse(int serverPort, UUID accessTokenId, UUID stockItemId, Integer inStorage) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(inStorage))
            .post(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_IN_STORAGE, "stockItemId", stockItemId));
    }

    public static void editInStorage(int serverPort, UUID accessTokenId, UUID stockItemId, Integer inStorage) {
        Response response = getEditInStorageResponse(serverPort, accessTokenId, stockItemId, inStorage);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getMoveStockToCarResponse(int serverPort, UUID accessTokenId, UUID stockItemId, Integer amount) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(amount))
            .post(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_MOVE_STOCK_TO_CAR, "stockItemId", stockItemId));
    }

    public static List<StockItemInventoryResponse> moveStockToCar(int serverPort, UUID accessTokenId, UUID stockItemId, Integer amount) {
        Response response = getMoveStockToCarResponse(serverPort, accessTokenId, stockItemId, amount);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StockItemInventoryResponse[].class));
    }

    public static Response getMoveStockToStorageResponse(int serverPort, UUID accessTokenId, UUID stockItemId, int amount) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(amount))
            .post(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_MOVE_STOCK_TO_STORAGE, "stockItemId", stockItemId));
    }

    public static List<StockItemInventoryResponse> moveStockToStorage(int serverPort, UUID accessTokenId, UUID stockItemId, Integer amount) {
        Response response = getMoveStockToStorageResponse(serverPort, accessTokenId, stockItemId, amount);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StockItemInventoryResponse[].class));
    }

    public static Response getEditBarCodeResponse(int serverPort, UUID accessTokenId, UUID stockItemId, String barCode) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(barCode))
            .post(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_BAR_CODE, "stockItemId", stockItemId));
    }

    public static void editBarCode(int serverPort, UUID accessTokenId, UUID stockItemId, String barCode) {
        Response response = getEditBarCodeResponse(serverPort, accessTokenId, stockItemId, barCode);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static List<StockItemInventoryResponse> resetInventoried(int serverPort, UUID accessTokenId) {
        Response response = getResetInventoriedResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StockItemInventoryResponse[].class));
    }

    public static Response getResetInventoriedResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_RESET_INVENTORIED));
    }

    public static Response getEditMarkedForAcquisitionResponse(int serverPort, UUID accessTokenId, UUID stockItemId, Boolean markedForAcquisition) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(markedForAcquisition))
            .post(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_STOCK_INVENTORY_EDIT_MARKED_FOR_ACQUISITION, "stockItemId", stockItemId));
    }
}
