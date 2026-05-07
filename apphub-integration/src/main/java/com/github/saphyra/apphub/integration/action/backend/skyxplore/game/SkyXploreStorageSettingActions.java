package com.github.saphyra.apphub.integration.action.backend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreGameEndpoints;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.StorageSettingModel;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreStorageSettingActions {
    public static List<StorageSettingModel> createStorageSetting(int serverPort, String accessToken, UUID planetId, StorageSettingModel model) {
        Response response = getCreateStorageSettingResponse(serverPort, accessToken, planetId, model);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StorageSettingModel[].class));
    }

    public static Response getCreateStorageSettingResponse(int serverPort, String accessToken, UUID planetId, StorageSettingModel model) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(model)
            .put(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_CREATE_STORAGE_SETTING, "planetId", planetId));
    }

    public static List<StorageSettingModel> getStorageSettings(int serverPort, String accessToken, UUID planetId) {
        Response response = getStorageSettingsResponse(serverPort, accessToken, planetId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StorageSettingModel[].class));
    }

    public static Response getStorageSettingsResponse(int serverPort, String accessToken, UUID planetId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_GET_STORAGE_SETTINGS, "planetId", planetId));
    }

    public static void deleteStorageSetting(int serverPort, String accessToken, UUID storageSettingId) {
        Response response = getDeleteStorageSettingResponse(serverPort, accessToken, storageSettingId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteStorageSettingResponse(int serverPort, String accessToken, UUID storageSettingId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .delete(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_DELETE_STORAGE_SETTING, "storageSettingId", storageSettingId));
    }

    public static List<StorageSettingModel> editStorageSetting(int serverPort, String accessToken, StorageSettingModel model) {
        Response response = getEditStorageSettingResponse(serverPort, accessToken, model);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StorageSettingModel[].class));
    }

    public static Response getEditStorageSettingResponse(int serverPort, String accessToken, StorageSettingModel model) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(model)
            .post(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_EDIT_STORAGE_SETTING));
    }
}
