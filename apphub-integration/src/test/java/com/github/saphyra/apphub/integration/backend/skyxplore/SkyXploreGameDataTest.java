package com.github.saphyra.apphub.integration.backend.skyxplore;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreGameDataActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreGameDataTest extends BackEndTest {
    @Test(groups = {"be", "skyxplore"})
    public void getGameData() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        SkyXploreCharacterModel model = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(accessTokenId, model);

        notFound(accessTokenId);
        get(accessTokenId);
    }

    private static void notFound(UUID accessTokenId) {
        Response notFoundResponse = SkyXploreGameDataActions.getGameDateResponse(accessTokenId, "asd");
        verifyErrorResponse(notFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void get(UUID accessTokenId) {
        Response getResponse = SkyXploreGameDataActions.getGameDateResponse(accessTokenId, "community_center");
        assertThat(getResponse.getStatusCode()).isEqualTo(200);
        assertThat(getResponse.getBody().jsonPath().getString("id")).isEqualTo("community_center");
    }
}
