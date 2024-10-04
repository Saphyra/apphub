package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreDataEndpoints;
import com.github.saphyra.apphub.integration.structure.api.OneParamResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.setting.SettingIdentifier;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.setting.SettingModel;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreSettingActions {
    public static Response getCreateOrUpdateSettingResponse(int serverPort, UUID accessTokenId, SettingModel settingModel) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(settingModel)
            .put(UrlFactory.create(serverPort, SkyXploreDataEndpoints.SKYXPLORE_DATA_CREATE_SETTING));
    }

    public static Response getSettingResponse(int serverPort, UUID accessTokenId, SettingIdentifier settingIdentifier) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(settingIdentifier)
            .post(UrlFactory.create(serverPort, SkyXploreDataEndpoints.SKYXPLORE_DATA_GET_SETTING));
    }

    public static Response getDeleteResponse(int serverPort, UUID accessTokenId, SettingIdentifier settingIdentifier) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(settingIdentifier)
            .delete(UrlFactory.create(serverPort, SkyXploreDataEndpoints.SKYXPLORE_DATA_DELETE_SETTING));
    }

    public static void createOrUpdate(int serverPort, UUID accessTokenId, SettingModel settingModel) {
        assertThat(getCreateOrUpdateSettingResponse(serverPort, accessTokenId, settingModel).getStatusCode()).isEqualTo(200);
    }

    public static SettingModel getSetting(int serverPort, UUID accessTokenId, SettingIdentifier settingIdentifier) {
        Response response = getSettingResponse(serverPort, accessTokenId, settingIdentifier);

        assertThat(response.getStatusCode()).isEqualTo(200);

        TypeRef<OneParamResponse<SettingModel>> typeRef = new TypeRef<>() {
        };

        return response.getBody().as(typeRef).getValue();
    }

    public static SettingModel delete(int serverPort, UUID accessTokenId, SettingIdentifier settingIdentifier) {
        Response response = getDeleteResponse(serverPort, accessTokenId, settingIdentifier);

        assertThat(response.getStatusCode()).isEqualTo(200);

        TypeRef<OneParamResponse<SettingModel>> typeRef = new TypeRef<>() {
        };

        return response.getBody().as(typeRef).getValue();
    }
}
