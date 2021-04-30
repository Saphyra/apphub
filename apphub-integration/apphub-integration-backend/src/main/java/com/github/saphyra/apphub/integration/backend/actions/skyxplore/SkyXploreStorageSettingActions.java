package com.github.saphyra.apphub.integration.backend.actions.skyxplore;

import com.github.saphyra.apphub.integration.backend.model.skyxplore.StorageSettingModel;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.StorageSettingsResponse;
import com.github.saphyra.apphub.integration.common.framework.BiWrapper;
import com.github.saphyra.apphub.integration.common.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.RequestFactory;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreStorageSettingActions {
    public static void createStorageSetting(Language language, UUID accessTokenId, UUID planetId, StorageSettingModel model) {
        Response response = getCreateStorageSettingResponse(language, accessTokenId, planetId, model);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCreateStorageSettingResponse(Language language, UUID accessTokenId, UUID planetId, StorageSettingModel model) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(model)
            .put(UrlFactory.create(Endpoints.SKYXPLORE_PLANET_CREATE_STORAGE_SETTING, "planetId", planetId));
    }

    public static StorageSettingsResponse getStorageSettings(Language language, UUID accessTokenId, UUID planetId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_PLANET_GET_STORAGE_SETTINGS, "planetId", planetId));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(StorageSettingsResponse.class);
    }

    public static void deleteStorageSetting(Language language, UUID accessTokenId, UUID planetId, UUID storageSettingId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .delete(UrlFactory.create(Endpoints.SKYXPLORE_PLANET_DELETE_STORAGE_SETTING, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("storageSettingId", storageSettingId))));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditStorageSettingResponse(Language language, UUID accessTokenId, UUID planetId, StorageSettingModel model) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(model)
            .post(UrlFactory.create(Endpoints.SKYXPLORE_PLANET_EDIT_STORAGE_SETTING, "planetId", planetId));
    }
}
