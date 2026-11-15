package com.github.saphyra.apphub.integration.backend.skyxplore.data;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreGameDataActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.framework.ValidationConstants;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GetAvailableConstructionAreasTest extends BackEndTest {
    /**
     * Tests retrieval of available construction areas for a given surface type and validation of input parameter handling.
     *
     * <ul>
     *   <li>validate endpoint {@code GET /api/skyxplore/game-data/available-construction-areas?surfaceType=DESERT} returns data when called with valid surface type</li>
     *   <li>Error message appears when user calls the same endpoint with invalid surface type</li>
     * </ul>
     */
    @Test(groups = {"be", "skyxplore"})
    public void getAvailableConstructionAreas() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        invalidSurfaceType(accessToken);
        getAvailable(accessToken);
    }

    private void getAvailable(String accessToken) {
        assertThat(SkyXploreGameDataActions.getAvailableConstructionAreas(getServerPort(), accessToken, Constants.SURFACE_TYPE_DESERT));
    }

    private void invalidSurfaceType(String accessToken) {
        ResponseValidator.verifyInvalidParam(SkyXploreGameDataActions.getAvailableConstructionAreasResponse(getServerPort(), accessToken, "asd"), "surfaceType", ValidationConstants.INVALID_VALUE);
    }
}
