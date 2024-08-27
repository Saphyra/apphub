package com.github.saphyra.apphub.integration.backend.skyxplore.game.planet.terraform;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSurfaceActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PlanetOverviewResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.QueueResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SurfaceResponse;
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
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessTokenId, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(getServerPort(), new Player(accessTokenId, userId1));

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(getServerPort(), accessTokenId)
            .getPlanetId();

        UUID emptySurfaceId = SkyXplorePlanetActions.findEmptySurface(getServerPort(), accessTokenId, planetId, Constants.SURFACE_TYPE_DESERT);

        invalidSurfaceType(accessTokenId, planetId, emptySurfaceId);
        surfaceNotEmpty(accessTokenId, planetId);
        incompatibleSurfaceType(accessTokenId, planetId, emptySurfaceId);
        terraform(accessTokenId, planetId, emptySurfaceId);
        terraformationAlreadyInProgress(accessTokenId, planetId, emptySurfaceId);
        cancel(accessTokenId, planetId, emptySurfaceId);
    }

    private static void invalidSurfaceType(UUID accessTokenId, UUID planetId, UUID emptySurfaceId) {
        Response invalidSurfaceTypeResponse = SkyXploreSurfaceActions.getTerraformResponse(getServerPort(), accessTokenId, planetId, emptySurfaceId, "asd");

        ResponseValidator.verifyInvalidParam(invalidSurfaceTypeResponse, "surfaceType", "invalid value");
    }

    private void surfaceNotEmpty(UUID accessTokenId, UUID planetId) {
        UUID occupiedSurfaceId = findOccupiedMilitary(accessTokenId, planetId);

        Response surfaceOccupiedResponse = SkyXploreSurfaceActions.getTerraformResponse(getServerPort(), accessTokenId, planetId, occupiedSurfaceId, Constants.SURFACE_TYPE_LAKE);

        ResponseValidator.verifyForbiddenOperation(surfaceOccupiedResponse);
    }

    private static void incompatibleSurfaceType(UUID accessTokenId, UUID planetId, UUID emptySurfaceId) {
        Response incompatibleSurfaceTypeResponse = SkyXploreSurfaceActions.getTerraformResponse(getServerPort(), accessTokenId, planetId, emptySurfaceId, Constants.SURFACE_TYPE_OIL_FIELD);

        ResponseValidator.verifyForbiddenOperation(incompatibleSurfaceTypeResponse);
    }

    private void terraform(UUID accessTokenId, UUID planetId, UUID emptySurfaceId) {
        SkyXploreSurfaceActions.terraform(getServerPort(), accessTokenId, planetId, emptySurfaceId, Constants.SURFACE_TYPE_LAKE);

        PlanetOverviewResponse planetOverviewResponse = SkyXplorePlanetActions.getPlanetOverview(getServerPort(), accessTokenId, planetId);
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

    private static void terraformationAlreadyInProgress(UUID accessTokenId, UUID planetId, UUID emptySurfaceId) {
        Response alreadyInProgressResponse = SkyXploreSurfaceActions.getTerraformResponse(getServerPort(), accessTokenId, planetId, emptySurfaceId, Constants.SURFACE_TYPE_LAKE);

        ResponseValidator.verifyErrorResponse(alreadyInProgressResponse, 409, ErrorCode.ALREADY_EXISTS);
    }

    private void cancel(UUID accessTokenId, UUID planetId, UUID emptySurfaceId) {
        SkyXploreSurfaceActions.cancelTerraformation(getServerPort(), accessTokenId, planetId, emptySurfaceId);

        PlanetOverviewResponse planetOverviewResponse = SkyXplorePlanetActions.getPlanetOverview(getServerPort(), accessTokenId, planetId);

        SurfaceResponse modifiedSurfaceResponse = SkyXplorePlanetActions.findSurfaceBySurfaceId(planetOverviewResponse.getSurfaces(), emptySurfaceId)
            .orElseThrow(() -> new RuntimeException("Surface not found."));

        assertThat(modifiedSurfaceResponse.getTerraformation()).isNull();
    }

    @Test(groups = {"be", "skyxplore"})
    public void finishTerraformation() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        Integer serverPort = getServerPort();
        UUID accessTokenId = IndexPageActions.registerAndLogin(serverPort, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(serverPort, accessTokenId, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(serverPort, new Player(accessTokenId, userId1));

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(serverPort, accessTokenId)
            .getPlanetId();

        UUID surfaceId = SkyXploreSurfaceActions.findEmptySurfaceId(serverPort, accessTokenId, planetId, Constants.SURFACE_TYPE_DESERT);

        SkyXploreSurfaceActions.terraform(serverPort, accessTokenId, planetId, surfaceId, Constants.SURFACE_TYPE_CONCRETE);

        SkyXploreGameActions.setPaused(serverPort, accessTokenId, false);

        AwaitilityWrapper.create(120, 5)
            .until(() -> isTerraformationFinished(serverPort, accessTokenId, planetId, surfaceId))
            .assertTrue("Terraformation is not finished.");
    }

    private static boolean isTerraformationFinished(int serverPort, UUID accessTokenId, UUID planetId, UUID surfaceId) {
        SurfaceResponse surfaceResponse = SkyXplorePlanetActions.findSurfaceBySurfaceId(SkyXplorePlanetActions.getSurfaces(serverPort, accessTokenId, planetId), surfaceId)
            .orElseThrow(() -> new RuntimeException("Surface not found"));
        return surfaceResponse.getSurfaceType().equals(Constants.SURFACE_TYPE_CONCRETE) && isNull(surfaceResponse.getTerraformation());
    }

    private UUID findOccupiedMilitary(UUID accessTokenId, UUID planetId) {
        return SkyXplorePlanetActions.getSurfaces(getServerPort(), accessTokenId, planetId)
            .stream()
            .filter(surfaceResponse -> !isNull(surfaceResponse.getBuilding()))
            .filter(surfaceResponse -> surfaceResponse.getSurfaceType().equals(Constants.SURFACE_TYPE_MILITARY))
            .findFirst()
            .map(SurfaceResponse::getSurfaceId)
            .orElseThrow(() -> new RuntimeException("Occupied Military not found on planet " + planetId));
    }
}
