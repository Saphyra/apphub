package com.github.saphyra.apphub.integration.action.backend.villany_atesz;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.StockCategoryModel;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class VillanyAteszStockCategoryActions {
    public static Response getCreateResponse(UUID accessTokenId, StockCategoryModel request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .put(UrlFactory.create(Endpoints.VILLANY_ATESZ_CREATE_STOCK_CATEGORY));
    }

    public static List<StockCategoryModel> create(UUID accessTokenId, StockCategoryModel request) {
        Response response = getCreateResponse(accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StockCategoryModel[].class));
    }

    public static List<StockCategoryModel> getStockCategories(UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.VILLANY_ATESZ_GET_STOCK_CATEGORIES));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StockCategoryModel[].class));
    }

    public static Response getEditResponse(UUID accessTokenId, UUID stockCategoryId, StockCategoryModel request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_EDIT_STOCK_CATEGORY, "stockCategoryId", stockCategoryId));
    }

    public static List<StockCategoryModel> edit(UUID accessTokenId, UUID stockCategoryId, StockCategoryModel request) {
        Response response = getEditResponse(accessTokenId, stockCategoryId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StockCategoryModel[].class));
    }

    public static List<StockCategoryModel> delete(UUID accessTokenId, UUID stockCategoryId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.VILLANY_ATESZ_EDIT_STOCK_CATEGORY, "stockCategoryId", stockCategoryId));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StockCategoryModel[].class));
    }
}
