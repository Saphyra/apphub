package com.github.saphyra.apphub.integration.backend.skyxplore.data;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreGameDataActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GetAvailableConstructionAreasTest extends BackEndTest {
    @Test(groups = {"be", "skyxplore"})
    public void getAvailableConstructionAreas() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        invalidSurfaceType(accessTokenId);
        getAvailable(accessTokenId);
    }

    private void getAvailable(UUID accessTokenId) {
        assertThat(SkyXploreGameDataActions.getAvailableConstructionAreas(getServerPort(), accessTokenId, Constants.SURFACE_TYPE_DESERT));
    }

    private void invalidSurfaceType(UUID accessTokenId) {
        ResponseValidator.verifyInvalidParam(SkyXploreGameDataActions.getAvailableConstructionAreasResponse(getServerPort(), accessTokenId, "asd"), "surfaceType", "invalid value");
    }
}
