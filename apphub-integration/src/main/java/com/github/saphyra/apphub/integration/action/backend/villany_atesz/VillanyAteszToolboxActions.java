package com.github.saphyra.apphub.integration.action.backend.villany_atesz;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.CreateToolRequest;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ToolResponse;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ToolStatus;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class VillanyAteszToolboxActions {
    public static void createTool(UUID accessTokenId, CreateToolRequest request) {
        Response response = getCreateToolResponse(accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCreateToolResponse(UUID accessTokenId, CreateToolRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .put(UrlFactory.create(Endpoints.VILLANY_ATESZ_CREATE_TOOL));
    }

    public static List<ToolResponse> getTools(UUID accessTokenId) {
        Response response = getToolsResponse(accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(ToolResponse[].class));
    }

    public static Response getToolsResponse(UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.VILLANY_ATESZ_GET_TOOLS));
    }

    public static Response getSetToolStatusResponse(UUID accessTokenId, UUID toolId, ToolStatus status) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(status))
            .post(UrlFactory.create(Endpoints.VILLANY_ATESZ_SET_TOOL_STATUS, "toolId", toolId));
    }

    public static List<ToolResponse> setToolStatus(UUID accessTokenId, UUID toolId, ToolStatus status) {
        Response response = getSetToolStatusResponse(accessTokenId, toolId, status);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(ToolResponse[].class));
    }

    public static List<ToolResponse> delete(UUID accessTokenId, UUID toolId) {
        Response response = getDeleteResponse(accessTokenId, toolId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(ToolResponse[].class));
    }

    public static Response getDeleteResponse(UUID accessTokenId, UUID toolId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.VILLANY_ATESZ_DELETE_TOOL, "toolId", toolId));
    }
}
