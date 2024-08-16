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
    public static Response getEditStorageBoxResponse(UUID accessTokenId, UUID toolId, StorageBoxModel storageBoxModel) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(storageBoxModel)
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_STORAGE_BOX, "toolId", toolId));
    }

    public static StorageBoxModel editStorageBox(UUID accessTokenId, UUID toolId, StorageBoxModel storageBoxModel) {
        Response response = getEditStorageBoxResponse(accessTokenId, toolId, storageBoxModel);

        assertThat(response.getStatusCode()).isEqualTo(200);

        if (response.asByteArray().length == 0) {
            return null;
        }

        return response.getBody().as(StorageBoxModel.class);
    }

    public static Response getEditToolTypeResponse(UUID accessTokenId, UUID toolId, ToolTypeModel toolTypeModel) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(toolTypeModel)
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_TOOL_TYPE, "toolId", toolId));
    }

    public static ToolTypeModel editToolType(UUID accessTokenId, UUID toolId, ToolTypeModel toolTypeModel) {
        Response response = getEditToolTypeResponse(accessTokenId, toolId, toolTypeModel);

        assertThat(response.getStatusCode()).isEqualTo(200);

        if (response.asByteArray().length == 0) {
            return null;
        }

        return response.getBody().as(ToolTypeModel.class);
    }

    public static Response getEditBrandResponse(UUID accessTokenId, UUID toolId, String brand) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(brand))
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_BRAND, "toolId", toolId));
    }

    public static void editBrand(UUID accessTokenId, UUID toolId, String brand) {
        Response response = getEditBrandResponse(accessTokenId, toolId, brand);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditNameResponse(UUID accessTokenId, UUID toolId, String name) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(name))
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_NAME, "toolId", toolId));
    }

    public static void editName(UUID accessTokenId, UUID toolId, String name) {
        assertThat(getEditNameResponse(accessTokenId, toolId, name).getStatusCode()).isEqualTo(200);
    }

    public static Response getEditCostResponse(UUID accessTokenId, UUID toolId, Integer cost) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(cost))
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_COST, "toolId", toolId));
    }

    public static void editCost(UUID accessTokenId, UUID toolId, Integer cost) {
        assertThat(getEditCostResponse(accessTokenId, toolId, cost).getStatusCode()).isEqualTo(200);
    }

    public static Response getEditAcquiredAtResponse(UUID accessTokenId, UUID toolId, LocalDate acquiredAt) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(acquiredAt))
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_ACQUIRED_AT, "toolId", toolId));
    }

    public static void editAcquiredAt(UUID accessTokenId, UUID toolId, LocalDate acquiredAt) {
        assertThat(getEditAcquiredAtResponse(accessTokenId, toolId, acquiredAt).getStatusCode()).isEqualTo(200);
    }

    public static Response getEditWarrantyExpiresAtResponse(UUID accessTokenId, UUID toolId, LocalDate warrantyExpiresAt) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(warrantyExpiresAt))
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_WARRANTY_EXPIRES_AT, "toolId", toolId));
    }

    public static void editWarrantyExpiresAt(UUID accessTokenId, UUID toolId, LocalDate warrantyExpiresAt) {
        assertThat(getEditWarrantyExpiresAtResponse(accessTokenId, toolId, warrantyExpiresAt).getStatusCode()).isEqualTo(200);
    }

    public static Response getEditStatusResponse(UUID accessTokenId, UUID toolId, ToolStatus status) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(status))
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_STATUS, "toolId", toolId));
    }

    public static void editStatus(UUID accessTokenId, UUID toolId, ToolStatus status) {
        assertThat(getEditStatusResponse(accessTokenId, toolId, status).getStatusCode()).isEqualTo(200);
    }

    public static Response getEditScrappedAtResponse(UUID accessTokenId, UUID toolId, LocalDate scrappedAt) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(scrappedAt))
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_SCRAPPED_AT, "toolId", toolId));
    }

    public static void editScrappedAt(UUID accessTokenId, UUID toolId, LocalDate scrappedAt) {
        assertThat(getEditScrappedAtResponse(accessTokenId, toolId, scrappedAt).getStatusCode()).isEqualTo(200);
    }

    public static Response getEditInventoriedResponse(UUID accessTokenId, UUID toolId, Boolean inventoried) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(inventoried))
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_INVENTORIED, "toolId", toolId));
    }

    public static void editInventoried(UUID accessTokenId, UUID toolId, boolean inventoried) {
        assertThat(getEditInventoriedResponse(accessTokenId, toolId, inventoried).getStatusCode()).isEqualTo(200);
    }

    public static List<ToolResponse> resetInventoried(UUID accessTokenId) {
        Response response = getResetInventoriedResponse(accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(ToolResponse[].class));
    }

    public static Response getResetInventoriedResponse(UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_RESET_INVENTORIED));
    }
}
