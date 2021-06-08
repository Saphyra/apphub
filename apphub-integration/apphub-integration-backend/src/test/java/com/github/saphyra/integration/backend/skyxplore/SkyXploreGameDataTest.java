package com.github.saphyra.integration.backend.skyxplore;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreGameDataActions;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreGameDataTest extends BackEndTest {
    @Test(dataProvider = "languageDataProvider", groups = "skyxplore")
    public void getGameData(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        SkyXploreCharacterModel model = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId, model);

        //Not found
        Response notFoundResponse = SkyXploreGameDataActions.getGameDateResponse(language, accessTokenId, "asd");
        verifyDataNotFound(language, notFoundResponse);

        //Get
        Response getResponse = SkyXploreGameDataActions.getGameDateResponse(language, accessTokenId, "community_center");
        assertThat(getResponse.getStatusCode()).isEqualTo(200);
        assertThat(getResponse.getBody().jsonPath().getString("id")).isEqualTo("community_center");
    }

    private void verifyDataNotFound(Language language, Response response) {
        assertThat(response.getStatusCode()).isEqualTo(404);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.DATA_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.DATA_NOT_FOUND));
    }
}
