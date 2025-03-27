package com.github.saphyra.apphub.integration.action.backend.villany_atesz;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.VillanyAteszEndpoints;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CommissionModel;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CommissionView;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class VillanyAteszCommissionActions {
    public static CommissionModel createOrUpdateCommission(int serverPort, UUID accessTokenId, CommissionModel request) {
        Response response = getCreateOrUpdateCommissionResponse(serverPort, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(CommissionModel.class);
    }

    public static Response getCreateOrUpdateCommissionResponse(int serverPort, UUID accessTokenId, CommissionModel request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .post(UrlFactory.create(serverPort, VillanyAteszEndpoints.VILLANY_ATESZ_COMMISSION_CREATE_OR_UPDATE));
    }

    public static Response getCommissionResponse(int serverPort, UUID accessTokenId, UUID commissionId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, VillanyAteszEndpoints.VILLANY_ATESZ_COMMISSION_GET, "commissionId", commissionId));
    }

    public static Response getCartResponse(int serverPort, UUID accessTokenId, UUID cartId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, VillanyAteszEndpoints.VILLANY_ATESZ_COMMISSION_GET_CART, "cartId", cartId));
    }

    public static Response getDeleteResponse(int serverPort, UUID accessTokenId, UUID commissionId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, VillanyAteszEndpoints.VILLANY_ATESZ_COMMISSION_DELETE, "commissionId", commissionId));
    }

    public static Response getCommissionsResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, VillanyAteszEndpoints.VILLANY_ATESZ_COMMISSIONS_GET));
    }

    public static List<CommissionView> getCommissions(int serverPort, UUID accessTokenId) {
        Response response = getCommissionsResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(CommissionView[].class));
    }

    public static List<CommissionView> delete(int serverPort, UUID accessTokenId, UUID commissionId) {
        Response response = getDeleteResponse(serverPort, accessTokenId, commissionId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(CommissionView[].class));
    }
}
