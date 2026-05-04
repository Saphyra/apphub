package com.github.saphyra.apphub.integration.backend.skyxplore.data;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreGameDataActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
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
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        notFound(accessToken);
        get(accessToken);
    }

    private static void notFound(String accessToken) {
        Response notFoundResponse = SkyXploreGameDataActions.getGameDateResponse(getServerPort(), accessToken, "asd");
        verifyErrorResponse(notFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void get(String accessToken) {
        Response getResponse = SkyXploreGameDataActions.getGameDateResponse(getServerPort(), accessToken, Constants.DATA_ID_HEADQUARTERS);
        assertThat(getResponse.getStatusCode()).isEqualTo(200);
        assertThat(getResponse.getBody().jsonPath().getString("id")).isEqualTo(Constants.DATA_ID_HEADQUARTERS);
    }
}
