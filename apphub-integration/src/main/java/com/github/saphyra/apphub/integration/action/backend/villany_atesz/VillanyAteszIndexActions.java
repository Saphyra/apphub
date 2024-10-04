package com.github.saphyra.apphub.integration.action.backend.villany_atesz;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.VillanyAteszEndpoints;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.StockItemResponse;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class VillanyAteszIndexActions {
    public static Integer getTotalStockValue(int serverPort, UUID accessTokenId) {
        Response response = getTotalStockValueResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().jsonPath().getInt("value");
    }

    public static Response getTotalStockValueResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, VillanyAteszEndpoints.VILLANY_ATESZ_INDEX_TOTAL_STOCK_VALUE));
    }

    public static Response getStockItemsMarkedForAcquisitionResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, VillanyAteszEndpoints.VILLANY_ATESZ_INDEX_GET_STOCK_ITEMS_MARKED_FOR_ACQUISITION));
    }

    public static List<StockItemResponse> getStockItemsMarkedForAcquisition(int serverPort, UUID accessTokenId) {
        Response response = getStockItemsMarkedForAcquisitionResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StockItemResponse[].class));
    }

    public static Integer getTotalToolboxValue(int serverPort, UUID accessTokenId) {
        Response response = getTotalToolboxValueResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().jsonPath().getInt("value");
    }

    public static Response getTotalToolboxValueResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, VillanyAteszEndpoints.VILLANY_ATESZ_INDEX_TOTAL_TOOLBOX_VALUE));
    }
}
