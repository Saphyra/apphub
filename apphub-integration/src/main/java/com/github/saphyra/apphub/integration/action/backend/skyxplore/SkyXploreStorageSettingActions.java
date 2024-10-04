package com.github.saphyra.apphub.integration.action.backend.skyxplore;

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
    public static List<StorageSettingModel> createStorageSetting(int serverPort, UUID accessTokenId, UUID planetId, StorageSettingModel model) {
        Response response = getCreateStorageSettingResponse(serverPort, accessTokenId, planetId, model);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StorageSettingModel[].class));
    }

    public static Response getCreateStorageSettingResponse(int serverPort, UUID accessTokenId, UUID planetId, StorageSettingModel model) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(model)
            .put(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_CREATE_STORAGE_SETTING, "planetId", planetId));
    }

    public static List<StorageSettingModel> getStorageSettings(int serverPort, UUID accessTokenId, UUID planetId) {
        Response response = getStorageSettingsResponse(serverPort, accessTokenId, planetId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StorageSettingModel[].class));
    }

    public static Response getStorageSettingsResponse(int serverPort, UUID accessTokenId, UUID planetId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_GET_STORAGE_SETTINGS, "planetId", planetId));
    }

    public static void deleteStorageSetting(int serverPort, UUID accessTokenId, UUID storageSettingId) {
        Response response = getDeleteStorageSettingResponse(serverPort, accessTokenId, storageSettingId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteStorageSettingResponse(int serverPort, UUID accessTokenId, UUID storageSettingId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_DELETE_STORAGE_SETTING, "storageSettingId", storageSettingId));
    }

    public static List<StorageSettingModel> editStorageSetting(int serverPort, UUID accessTokenId, StorageSettingModel model) {
        Response response = getEditStorageSettingResponse(serverPort, accessTokenId, model);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StorageSettingModel[].class));
    }

    public static Response getEditStorageSettingResponse(int serverPort, UUID accessTokenId, StorageSettingModel model) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(model)
            .post(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_EDIT_STORAGE_SETTING));
    }
}
