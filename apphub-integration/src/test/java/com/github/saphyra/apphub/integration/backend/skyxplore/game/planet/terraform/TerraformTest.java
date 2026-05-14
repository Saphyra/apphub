package com.github.saphyra.apphub.integration.backend.skyxplore.game.planet.terraform;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreSurfaceActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DynamoDbUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.QueueResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.game.PlanetOverviewResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.game.SurfaceResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class TerraformTest extends BackEndTest {
    @Test(groups = {"be", "skyxplore"})
    public void terraformCD() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken, characterModel1);
        UUID userId1 = DynamoDbUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(getServerPort(), new Player(accessToken, userId1));

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(getServerPort(), accessToken)
            .getPlanetId();

        UUID emptySurfaceId = SkyXplorePlanetActions.findEmptySurface(getServerPort(), accessToken, planetId, Constants.SURFACE_TYPE_DESERT);

        invalidSurfaceType(accessToken, planetId, emptySurfaceId);
        surfaceNotEmpty(accessToken, planetId);
        incompatibleSurfaceType(accessToken, planetId, emptySurfaceId);
        terraform(accessToken, planetId, emptySurfaceId);
        terraformationAlreadyInProgress(accessToken, planetId, emptySurfaceId);
        cancel(accessToken, planetId, emptySurfaceId);
    }

    private static void invalidSurfaceType(String accessToken, UUID planetId, UUID emptySurfaceId) {
        Response invalidSurfaceTypeResponse = SkyXploreSurfaceActions.getTerraformResponse(getServerPort(), accessToken, planetId, emptySurfaceId, "asd");

        ResponseValidator.verifyInvalidParam(invalidSurfaceTypeResponse, "surfaceType", "invalid value");
    }

    private void surfaceNotEmpty(String accessToken, UUID planetId) {
        UUID occupiedSurfaceId = findOccupied(accessToken, planetId);

        Response surfaceOccupiedResponse = SkyXploreSurfaceActions.getTerraformResponse(getServerPort(), accessToken, planetId, occupiedSurfaceId, Constants.SURFACE_TYPE_LAKE);

        ResponseValidator.verifyForbiddenOperation(surfaceOccupiedResponse);
    }

    private static void incompatibleSurfaceType(String accessToken, UUID planetId, UUID emptySurfaceId) {
        Response incompatibleSurfaceTypeResponse = SkyXploreSurfaceActions.getTerraformResponse(getServerPort(), accessToken, planetId, emptySurfaceId, Constants.SURFACE_TYPE_OIL_FIELD);

        ResponseValidator.verifyForbiddenOperation(incompatibleSurfaceTypeResponse);
    }

    private void terraform(String accessToken, UUID planetId, UUID emptySurfaceId) {
        SkyXploreSurfaceActions.terraform(getServerPort(), accessToken, planetId, emptySurfaceId, Constants.SURFACE_TYPE_LAKE);

        PlanetOverviewResponse planetOverviewResponse = SkyXplorePlanetActions.getPlanetOverview(getServerPort(), accessToken, planetId);
        SurfaceResponse modifiedSurfaceResponse = SkyXplorePlanetActions.findSurfaceBySurfaceId(planetOverviewResponse.getSurfaces(), emptySurfaceId)
            .orElseThrow(() -> new RuntimeException("Surface not found."));

        assertThat(modifiedSurfaceResponse.getTerraformation()).isNotNull();

        QueueResponse queueItemModifiedEvent = planetOverviewResponse.getQueue()
            .get(0);

        UUID constructionId = modifiedSurfaceResponse.getTerraformation().getConstructionId();
        assertThat(queueItemModifiedEvent.getItemId()).isEqualTo(constructionId);
        assertThat(queueItemModifiedEvent.getType()).isEqualTo(Constants.QUEUE_TYPE_TERRAFORMATION);
        assertThat(queueItemModifiedEvent.getData()).containsEntry("currentSurfaceType", Constants.SURFACE_TYPE_DESERT);
        assertThat(queueItemModifiedEvent.getData()).containsEntry("targetSurfaceType", Constants.SURFACE_TYPE_LAKE);
    }

    private static void terraformationAlreadyInProgress(String accessToken, UUID planetId, UUID emptySurfaceId) {
        Response alreadyInProgressResponse = SkyXploreSurfaceActions.getTerraformResponse(getServerPort(), accessToken, planetId, emptySurfaceId, Constants.SURFACE_TYPE_LAKE);

        ResponseValidator.verifyErrorResponse(alreadyInProgressResponse, 409, ErrorCode.ALREADY_EXISTS);
    }

    private void cancel(String accessToken, UUID planetId, UUID emptySurfaceId) {
        SkyXploreSurfaceActions.cancelTerraformation(getServerPort(), accessToken, planetId, emptySurfaceId);

        PlanetOverviewResponse planetOverviewResponse = SkyXplorePlanetActions.getPlanetOverview(getServerPort(), accessToken, planetId);

        SurfaceResponse modifiedSurfaceResponse = SkyXplorePlanetActions.findSurfaceBySurfaceId(planetOverviewResponse.getSurfaces(), emptySurfaceId)
            .orElseThrow(() -> new RuntimeException("Surface not found."));

        assertThat(modifiedSurfaceResponse.getTerraformation()).isNull();
    }

    @Test(groups = {"be", "skyxplore"})
    public void finishTerraformation() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        int serverPort = getServerPort();
        String accessToken = IndexPageActions.registerAndLogin(serverPort, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(serverPort, accessToken, characterModel1);
        UUID userId1 = DynamoDbUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(serverPort, new Player(accessToken, userId1));

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(serverPort, accessToken)
            .getPlanetId();

        UUID surfaceId = SkyXplorePlanetActions.findEmptySurface(serverPort, accessToken, planetId, Constants.SURFACE_TYPE_DESERT);

        SkyXploreSurfaceActions.terraform(serverPort, accessToken, planetId, surfaceId, Constants.SURFACE_TYPE_CONCRETE);

        SkyXploreGameActions.setPaused(serverPort, accessToken, false);

        AwaitilityWrapper.create(120, 5)
            .until(() -> isTerraformationFinished(serverPort, accessToken, planetId, surfaceId))
            .assertTrue("Terraformation is not finished.");
    }

    private static boolean isTerraformationFinished(int serverPort, String accessToken, UUID planetId, UUID surfaceId) {
        SurfaceResponse surfaceResponse = SkyXplorePlanetActions.findSurfaceBySurfaceId(SkyXplorePlanetActions.getSurfaces(serverPort, accessToken, planetId), surfaceId)
            .orElseThrow(() -> new RuntimeException("Surface not found"));
        return surfaceResponse.getSurfaceType().equals(Constants.SURFACE_TYPE_CONCRETE) && isNull(surfaceResponse.getTerraformation());
    }

    private UUID findOccupied(String accessToken, UUID planetId) {
        return SkyXplorePlanetActions.getSurfaces(getServerPort(), accessToken, planetId)
            .stream()
            .filter(surfaceResponse -> !isNull(surfaceResponse.getConstructionArea()))
            .findFirst()
            .map(SurfaceResponse::getSurfaceId)
            .orElseThrow(() -> new RuntimeException("Occupied surface not found on planet " + planetId));
    }
}
