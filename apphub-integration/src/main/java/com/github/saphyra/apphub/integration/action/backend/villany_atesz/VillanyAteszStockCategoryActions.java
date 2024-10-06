package com.github.saphyra.apphub.integration.action.backend.villany_atesz;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.VillanyAteszEndpoints;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.StockCategoryModel;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class VillanyAteszStockCategoryActions {
    public static Response getCreateResponse(int serverPort, UUID accessTokenId, StockCategoryModel request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .put(UrlFactory.create(serverPort, VillanyAteszEndpoints.VILLANY_ATESZ_CREATE_STOCK_CATEGORY));
    }

    public static List<StockCategoryModel> create(int serverPort, UUID accessTokenId, StockCategoryModel request) {
        Response response = getCreateResponse(serverPort, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StockCategoryModel[].class));
    }

    public static List<StockCategoryModel> getStockCategories(int serverPort, UUID accessTokenId) {
        Response response = getStockCategoriesResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StockCategoryModel[].class));
    }

    public static Response getStockCategoriesResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, VillanyAteszEndpoints.VILLANY_ATESZ_GET_STOCK_CATEGORIES));
    }

    public static Response getEditResponse(int serverPort, UUID accessTokenId, UUID stockCategoryId, StockCategoryModel request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .post(UrlFactory.create(serverPort, VillanyAteszEndpoints.VILLANY_ATESZ_EDIT_STOCK_CATEGORY, "stockCategoryId", stockCategoryId));
    }

    public static List<StockCategoryModel> edit(int serverPort, UUID accessTokenId, UUID stockCategoryId, StockCategoryModel request) {
        Response response = getEditResponse(serverPort, accessTokenId, stockCategoryId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StockCategoryModel[].class));
    }

    public static List<StockCategoryModel> delete(int serverPort, UUID accessTokenId, UUID stockCategoryId) {
        Response response = getDeleteResponse(serverPort, accessTokenId, stockCategoryId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StockCategoryModel[].class));
    }

    public static Response getDeleteResponse(int serverPort, UUID accessTokenId, UUID stockCategoryId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, VillanyAteszEndpoints.VILLANY_ATESZ_DELETE_STOCK_CATEGORY, "stockCategoryId", stockCategoryId));
    }
}
