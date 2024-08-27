package com.github.saphyra.apphub.integration.action.backend.villany_atesz;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.StorageBoxModel;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ToolResponse;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ToolStatus;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ToolTypeModel;
import io.restassured.response.Response;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class VillanyAteszToolboxInventoryActions {
    public static Response getEditStorageBoxResponse(int serverPort, UUID accessTokenId, UUID toolId, StorageBoxModel storageBoxModel) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(storageBoxModel)
            .post(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_STORAGE_BOX, "toolId", toolId));
    }

    public static StorageBoxModel editStorageBox(int serverPort, UUID accessTokenId, UUID toolId, StorageBoxModel storageBoxModel) {
        Response response = getEditStorageBoxResponse(serverPort, accessTokenId, toolId, storageBoxModel);

        assertThat(response.getStatusCode()).isEqualTo(200);

        if (response.asByteArray().length == 0) {
            return null;
        }

        return response.getBody().as(StorageBoxModel.class);
    }

    public static Response getEditToolTypeResponse(int serverPort, UUID accessTokenId, UUID toolId, ToolTypeModel toolTypeModel) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(toolTypeModel)
            .post(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_TOOL_TYPE, "toolId", toolId));
    }

    public static ToolTypeModel editToolType(int serverPort, UUID accessTokenId, UUID toolId, ToolTypeModel toolTypeModel) {
        Response response = getEditToolTypeResponse(serverPort, accessTokenId, toolId, toolTypeModel);

        assertThat(response.getStatusCode()).isEqualTo(200);

        if (response.asByteArray().length == 0) {
            return null;
        }

        return response.getBody().as(ToolTypeModel.class);
    }

    public static Response getEditBrandResponse(int serverPort, UUID accessTokenId, UUID toolId, String brand) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(brand))
            .post(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_BRAND, "toolId", toolId));
    }

    public static void editBrand(int serverPort, UUID accessTokenId, UUID toolId, String brand) {
        Response response = getEditBrandResponse(serverPort, accessTokenId, toolId, brand);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditNameResponse(int serverPort, UUID accessTokenId, UUID toolId, String name) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(name))
            .post(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_NAME, "toolId", toolId));
    }

    public static void editName(int serverPort, UUID accessTokenId, UUID toolId, String name) {
        assertThat(getEditNameResponse(serverPort, accessTokenId, toolId, name).getStatusCode()).isEqualTo(200);
    }

    public static Response getEditCostResponse(int serverPort, UUID accessTokenId, UUID toolId, Integer cost) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(cost))
            .post(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_COST, "toolId", toolId));
    }

    public static void editCost(int serverPort, UUID accessTokenId, UUID toolId, Integer cost) {
        assertThat(getEditCostResponse(serverPort, accessTokenId, toolId, cost).getStatusCode()).isEqualTo(200);
    }

    public static Response getEditAcquiredAtResponse(int serverPort, UUID accessTokenId, UUID toolId, LocalDate acquiredAt) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(acquiredAt))
            .post(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_ACQUIRED_AT, "toolId", toolId));
    }

    public static void editAcquiredAt(int serverPort, UUID accessTokenId, UUID toolId, LocalDate acquiredAt) {
        assertThat(getEditAcquiredAtResponse(serverPort, accessTokenId, toolId, acquiredAt).getStatusCode()).isEqualTo(200);
    }

    public static Response getEditWarrantyExpiresAtResponse(int serverPort, UUID accessTokenId, UUID toolId, LocalDate warrantyExpiresAt) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(warrantyExpiresAt))
            .post(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_WARRANTY_EXPIRES_AT, "toolId", toolId));
    }

    public static void editWarrantyExpiresAt(int serverPort, UUID accessTokenId, UUID toolId, LocalDate warrantyExpiresAt) {
        assertThat(getEditWarrantyExpiresAtResponse(serverPort, accessTokenId, toolId, warrantyExpiresAt).getStatusCode()).isEqualTo(200);
    }

    public static Response getEditStatusResponse(int serverPort, UUID accessTokenId, UUID toolId, ToolStatus status) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(status))
            .post(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_STATUS, "toolId", toolId));
    }

    public static void editStatus(int serverPort, UUID accessTokenId, UUID toolId, ToolStatus status) {
        assertThat(getEditStatusResponse(serverPort, accessTokenId, toolId, status).getStatusCode()).isEqualTo(200);
    }

    public static Response getEditScrappedAtResponse(int serverPort, UUID accessTokenId, UUID toolId, LocalDate scrappedAt) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(scrappedAt))
            .post(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_SCRAPPED_AT, "toolId", toolId));
    }

    public static void editScrappedAt(int serverPort, UUID accessTokenId, UUID toolId, LocalDate scrappedAt) {
        assertThat(getEditScrappedAtResponse(serverPort, accessTokenId, toolId, scrappedAt).getStatusCode()).isEqualTo(200);
    }

    public static Response getEditInventoriedResponse(int serverPort, UUID accessTokenId, UUID toolId, Boolean inventoried) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(inventoried))
            .post(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_INVENTORIED, "toolId", toolId));
    }

    public static void editInventoried(int serverPort, UUID accessTokenId, UUID toolId, boolean inventoried) {
        assertThat(getEditInventoriedResponse(serverPort, accessTokenId, toolId, inventoried).getStatusCode()).isEqualTo(200);
    }

    public static List<ToolResponse> resetInventoried(int serverPort, UUID accessTokenId) {
        Response response = getResetInventoriedResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(ToolResponse[].class));
    }

    public static Response getResetInventoriedResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_RESET_INVENTORIED));
    }
}
