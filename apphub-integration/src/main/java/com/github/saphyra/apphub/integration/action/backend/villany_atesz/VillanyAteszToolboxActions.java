package com.github.saphyra.apphub.integration.action.backend.villany_atesz;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CreateToolRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.StorageBoxModel;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ToolResponse;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ToolStatus;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ToolTypeModel;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class VillanyAteszToolboxActions {
    public static void createTool(int serverPort, UUID accessTokenId, CreateToolRequest request) {
        Response response = getCreateToolResponse(serverPort, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCreateToolResponse(int serverPort, UUID accessTokenId, CreateToolRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_CREATE_TOOL));
    }

    public static List<ToolResponse> getTools(int serverPort, UUID accessTokenId) {
        Response response = getToolsResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(ToolResponse[].class));
    }

    public static Response getToolsResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_GET_TOOLS));
    }

    public static Response getSetToolStatusResponse(int serverPort, UUID accessTokenId, UUID toolId, ToolStatus status) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(status))
            .post(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_SET_TOOL_STATUS, "toolId", toolId));
    }

    public static List<ToolResponse> setToolStatus(int serverPort, UUID accessTokenId, UUID toolId, ToolStatus status) {
        Response response = getSetToolStatusResponse(serverPort, accessTokenId, toolId, status);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(ToolResponse[].class));
    }

    public static List<ToolResponse> delete(int serverPort, UUID accessTokenId, UUID toolId) {
        Response response = getDeleteResponse(serverPort, accessTokenId, toolId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(ToolResponse[].class));
    }

    public static Response getDeleteResponse(int serverPort, UUID accessTokenId, UUID toolId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_DELETE_TOOL, "toolId", toolId));
    }

    public static Response getToolTypesResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_GET_TOOL_TYPES));
    }

    public static Response getStorageBoxesResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.VILLANY_ATESZ_GET_STORAGE_BOXES));
    }

    public static List<StorageBoxModel> getStorageBoxes(int serverPort, UUID accessTokenId) {
        Response response = getStorageBoxesResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StorageBoxModel[].class));
    }

    public static List<ToolTypeModel> getToolTypes(int serverPort, UUID accessTokenId) {
        Response response = getToolTypesResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(ToolTypeModel[].class));
    }
}
