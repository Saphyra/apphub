package com.github.saphyra.integration.backend.skyxplore;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreGameDataActions;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.github.saphyra.apphub.integration.backend.ResponseValidator.verifyErrorResponse;
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
        verifyErrorResponse(language, notFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);

        //Get
        Response getResponse = SkyXploreGameDataActions.getGameDateResponse(language, accessTokenId, "community_center");
        assertThat(getResponse.getStatusCode()).isEqualTo(200);
        assertThat(getResponse.getBody().jsonPath().getString("id")).isEqualTo("community_center");
    }
}
