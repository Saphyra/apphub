package com.github.saphyra.apphub.integration.backend.skyxplore.game.planet.construction;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreConstructionAreaActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreSurfaceActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.game.SurfaceConstructionAreaResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.game.SurfaceResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;


public class ConstructionAreaCrudTest extends BackEndTest {
    @Test(groups = {"be", "skyxplore"})
    public void constructionAreaCrud() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken, characterModel1);
        UUID userId1 = UserDynamoDbRepository.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(getServerPort(), Constants.DEFAULT_GAME_NAME, new Player(accessToken, userId1));

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(getServerPort(), accessToken)
            .getPlanetId();

        construct_nullDataId(accessToken, planetId);
        construct_surfaceNotEmpty(accessToken, planetId);
        construct_unsupportedType(accessToken, planetId);
        construct_terraformationInProgress(accessToken, planetId);
        UUID surfaceId = construct(accessToken, planetId);
        deconstruct_underConstruction(accessToken, planetId, surfaceId);
        cancelConstruction(accessToken, planetId, surfaceId);
        surfaceId = construction_finish(accessToken, planetId);
        deconstruct(accessToken, planetId, surfaceId);
        deconstruct_alreadyUnderDeconstruction(accessToken, planetId, surfaceId);
        deconstruct_cancel(accessToken, planetId, surfaceId);
        deconstruct_finish(accessToken, planetId, surfaceId);
    }

    private void construct_nullDataId(String accessToken, UUID planetId) {
        UUID surfaceId = SkyXplorePlanetActions.findOccupiedSurfaceId(getServerPort(), accessToken, planetId);

        ResponseValidator.verifyInvalidParam(SkyXploreConstructionAreaActions.getConstructConstructionAreaResponse(getServerPort(), accessToken, surfaceId, null), "dataId", "must not be null");
    }

    private static void deconstruct_finish(String accessToken, UUID planetId, UUID surfaceId) {
        int serverPort = getServerPort();
        UUID constructionAreaId = SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessToken, planetId, surfaceId)
            .getConstructionArea()
            .getConstructionAreaId();
        SkyXploreConstructionAreaActions.deconstructConstructionArea(serverPort, accessToken, constructionAreaId);

        SkyXploreGameActions.setPaused(serverPort, accessToken, false);

        AwaitilityWrapper.create(120, 5)
            .until(() -> isNull(SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessToken, planetId, surfaceId).getConstructionArea()))
            .assertTrue("ConstructionArea is not deconstructed");
    }

    private static void deconstruct_cancel(String accessToken, UUID planetId, UUID surfaceId) {
        int serverPort = getServerPort();
        UUID deconstructionId = SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessToken, planetId, surfaceId)
            .getConstructionArea()
            .getDeconstruction()
            .getDeconstructionId();
        SkyXploreConstructionAreaActions.cancelDeconstructionOfConstructionArea(serverPort, accessToken, deconstructionId);

        AwaitilityWrapper.awaitAssert(
            () -> SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessToken, planetId, surfaceId),
            surfaceResponse -> assertThat(surfaceResponse)
                .extracting(SurfaceResponse::getConstructionArea)
                .extracting(SurfaceConstructionAreaResponse::getDeconstruction)
                .isNull()
        );
    }

    private static void deconstruct_alreadyUnderDeconstruction(String accessToken, UUID planetId, UUID surfaceId) {
        int serverPort = getServerPort();
        UUID constructionAreaId = SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessToken, planetId, surfaceId)
            .getConstructionArea()
            .getConstructionAreaId();
        ResponseValidator.verifyForbiddenOperation(SkyXploreConstructionAreaActions.getDeconstructConstructionAreaResponse(serverPort, accessToken, constructionAreaId));
    }

    private static void deconstruct(String accessToken, UUID planetId, UUID surfaceId) {
        int serverPort = getServerPort();
        UUID constructionAreaId = SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessToken, planetId, surfaceId)
            .getConstructionArea()
            .getConstructionAreaId();
        SkyXploreConstructionAreaActions.deconstructConstructionArea(serverPort, accessToken, constructionAreaId);

        AwaitilityWrapper.awaitAssert(
            () -> SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessToken, planetId, surfaceId),
            surfaceResponse -> assertThat(surfaceResponse)
                .extracting(SurfaceResponse::getConstructionArea)
                .extracting(SurfaceConstructionAreaResponse::getDeconstruction)
                .isNotNull()
        );
    }

    private static UUID construction_finish(String accessToken, UUID planetId) {
        int serverPort = getServerPort();
        UUID surfaceId = SkyXplorePlanetActions.findEmptySurface(serverPort, accessToken, planetId, Constants.SURFACE_TYPE_DESERT);
        SkyXploreConstructionAreaActions.constructConstructionArea(serverPort, accessToken, surfaceId, Constants.CONSTRUCTION_AREA_EXTRACTOR);

        SkyXploreGameActions.setPaused(serverPort, accessToken, false);

        AwaitilityWrapper.create(120, 5)
            .until(() -> isNull(SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessToken, planetId, surfaceId).getConstructionArea().getConstruction()))
            .assertTrue("Construction area construction is not finished.");

        SkyXploreGameActions.setPaused(serverPort, accessToken, true);

        return surfaceId;
    }

    private static void cancelConstruction(String accessToken, UUID planetId, UUID surfaceId) {
        int serverPort = getServerPort();

        UUID constructionId = SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessToken, planetId, surfaceId)
            .getConstructionArea()
            .getConstruction()
            .getConstructionId();
        SkyXploreConstructionAreaActions.cancelConstructionAreaConstruction(serverPort, accessToken, constructionId);

        AwaitilityWrapper.awaitAssert(
            () -> SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessToken, planetId, surfaceId),
            surfaceResponse -> assertThat(surfaceResponse)
                .extracting(SurfaceResponse::getConstructionArea)
                .isNull()
        );
    }

    private static void deconstruct_underConstruction(String accessToken, UUID planetId, UUID surfaceId) {
        UUID constructionAreaId = SkyXplorePlanetActions.findSurfaceBySurfaceId(getServerPort(), accessToken, planetId, surfaceId)
            .getConstructionArea()
            .getConstructionAreaId();
        ResponseValidator.verifyForbiddenOperation(SkyXploreConstructionAreaActions.getDeconstructConstructionAreaResponse(getServerPort(), accessToken, constructionAreaId));
    }

    private static UUID construct(String accessToken, UUID planetId) {
        int serverPort = getServerPort();
        UUID surfaceId = SkyXplorePlanetActions.findEmptySurface(serverPort, accessToken, planetId, Constants.SURFACE_TYPE_DESERT);
        SkyXploreConstructionAreaActions.constructConstructionArea(serverPort, accessToken, surfaceId, Constants.CONSTRUCTION_AREA_EXTRACTOR);

        AwaitilityWrapper.awaitAssert(
            () -> SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessToken, planetId, surfaceId),
            surfaceResponse -> assertThat(surfaceResponse)
                .extracting(SurfaceResponse::getConstructionArea)
                .returns(Constants.CONSTRUCTION_AREA_EXTRACTOR, SurfaceConstructionAreaResponse::getDataId)
                .extracting(SurfaceConstructionAreaResponse::getConstruction)
                .isNotNull()
        );

        return surfaceId;
    }

    private static void construct_terraformationInProgress(String accessToken, UUID planetId) {
        UUID surfaceId = SkyXplorePlanetActions.findEmptySurface(getServerPort(), accessToken, planetId, Constants.SURFACE_TYPE_LAKE);
        SkyXploreSurfaceActions.terraform(getServerPort(), accessToken, planetId, surfaceId, Constants.SURFACE_TYPE_FOREST);

        ResponseValidator.verifyErrorResponse(
            SkyXploreConstructionAreaActions.getConstructConstructionAreaResponse(getServerPort(), accessToken, surfaceId, Constants.CONSTRUCTION_AREA_EXTRACTOR),
            409,
            ErrorCode.ALREADY_EXISTS
        );
    }

    private static void construct_unsupportedType(String accessToken, UUID planetId) {
        UUID surfaceId = SkyXplorePlanetActions.findEmptySurface(getServerPort(), accessToken, planetId, Constants.SURFACE_TYPE_LAKE);

        ResponseValidator.verifyForbiddenOperation(SkyXploreConstructionAreaActions.getConstructConstructionAreaResponse(getServerPort(), accessToken, surfaceId, Constants.CONSTRUCTION_AREA_DEPOT));
    }

    private static void construct_surfaceNotEmpty(String accessToken, UUID planetId) {
        UUID surfaceId = SkyXplorePlanetActions.findOccupiedSurfaceId(getServerPort(), accessToken, planetId);

        ResponseValidator.verifyForbiddenOperation(SkyXploreConstructionAreaActions.getConstructConstructionAreaResponse(getServerPort(), accessToken, surfaceId, Constants.CONSTRUCTION_AREA_DEPOT));
    }
}
