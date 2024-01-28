package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.StorageSettingModel;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreStorageSettingActions {
    public static List<StorageSettingModel> createStorageSetting(UUID accessTokenId, UUID planetId, StorageSettingModel model) {
        Response response = getCreateStorageSettingResponse(accessTokenId, planetId, model);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StorageSettingModel[].class));
    }

    public static Response getCreateStorageSettingResponse(UUID accessTokenId, UUID planetId, StorageSettingModel model) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(model)
            .put(UrlFactory.create(Endpoints.SKYXPLORE_PLANET_CREATE_STORAGE_SETTING, "planetId", planetId));
    }

    public static List<StorageSettingModel> getStorageSettings(UUID accessTokenId, UUID planetId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_PLANET_GET_STORAGE_SETTINGS, "planetId", planetId));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StorageSettingModel[].class));
    }

    public static void deleteStorageSetting(UUID accessTokenId, UUID storageSettingId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.SKYXPLORE_PLANET_DELETE_STORAGE_SETTING, "storageSettingId", storageSettingId));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static List<StorageSettingModel> editStorageSetting(UUID accessTokenId, StorageSettingModel model) {
        Response response = getEditStorageSettingResponse(accessTokenId, model);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(StorageSettingModel[].class));
    }

    public static Response getEditStorageSettingResponse(UUID accessTokenId, StorageSettingModel model) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(model)
            .post(UrlFactory.create(Endpoints.SKYXPLORE_PLANET_EDIT_STORAGE_SETTING));
    }
}
