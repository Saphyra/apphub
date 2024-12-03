package com.github.saphyra.apphub.integration.backend.skyxplore.game.planet.construction;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreConstructionAreaActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreSurfaceActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
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
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessTokenId, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(getServerPort(), Constants.DEFAULT_GAME_NAME, new Player(accessTokenId, userId1));

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(getServerPort(), accessTokenId)
            .getPlanetId();

        construct_surfaceNotEmpty(accessTokenId, planetId);
        construct_unsupportedType(accessTokenId, planetId);
        construct_terraformationInProgress(accessTokenId, planetId);
        UUID surfaceId = construct(accessTokenId, planetId);
        deconstruct_underConstruction(accessTokenId, planetId, surfaceId);
        cancelConstruction(accessTokenId, planetId, surfaceId);
        surfaceId = construction_finish(accessTokenId, planetId);
        deconstruct(accessTokenId, planetId, surfaceId);
        deconstruct_alreadyUnderDeconstruction(accessTokenId, planetId, surfaceId);
        deconstruct_cancel(accessTokenId, planetId, surfaceId);
        deconstruct_finish(accessTokenId, planetId, surfaceId);
    }

    private static void deconstruct_finish(UUID accessTokenId, UUID planetId, UUID surfaceId) {
        int serverPort = getServerPort();
        UUID constructionAreaId = SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessTokenId, planetId, surfaceId)
            .getConstructionArea()
            .getConstructionAreaId();
        SkyXploreConstructionAreaActions.deconstructConstructionArea(serverPort, accessTokenId, constructionAreaId);

        SkyXploreGameActions.setPaused(serverPort, accessTokenId, false);

        AwaitilityWrapper.create(120, 5)
            .until(() -> isNull(SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessTokenId, planetId, surfaceId).getConstructionArea()))
            .assertTrue("ConstructionArea is not deconstructed");
    }

    private static void deconstruct_cancel(UUID accessTokenId, UUID planetId, UUID surfaceId) {
        int serverPort = getServerPort();
        UUID deconstructionId = SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessTokenId, planetId, surfaceId)
            .getConstructionArea()
            .getDeconstruction()
            .getDeconstructionId();
        SkyXploreConstructionAreaActions.cancelDeconstructionOfConstructionArea(serverPort, accessTokenId, deconstructionId);

        AwaitilityWrapper.awaitAssert(
            () -> SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessTokenId, planetId, surfaceId),
            surfaceResponse -> assertThat(surfaceResponse)
                .extracting(SurfaceResponse::getConstructionArea)
                .extracting(SurfaceConstructionAreaResponse::getDeconstruction)
                .isNull()
        );
    }

    private static void deconstruct_alreadyUnderDeconstruction(UUID accessTokenId, UUID planetId, UUID surfaceId) {
        int serverPort = getServerPort();
        UUID constructionAreaId = SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessTokenId, planetId, surfaceId)
            .getConstructionArea()
            .getConstructionAreaId();
        ResponseValidator.verifyForbiddenOperation(SkyXploreConstructionAreaActions.getDeconstructConstructionAreaResponse(serverPort, accessTokenId, constructionAreaId));
    }

    private static void deconstruct(UUID accessTokenId, UUID planetId, UUID surfaceId) {
        int serverPort = getServerPort();
        UUID constructionAreaId = SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessTokenId, planetId, surfaceId)
            .getConstructionArea()
            .getConstructionAreaId();
        SkyXploreConstructionAreaActions.deconstructConstructionArea(serverPort, accessTokenId, constructionAreaId);

        AwaitilityWrapper.awaitAssert(
            () -> SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessTokenId, planetId, surfaceId),
            surfaceResponse -> assertThat(surfaceResponse)
                .extracting(SurfaceResponse::getConstructionArea)
                .extracting(SurfaceConstructionAreaResponse::getDeconstruction)
                .isNotNull()
        );
    }

    private static UUID construction_finish(UUID accessTokenId, UUID planetId) {
        int serverPort = getServerPort();
        UUID surfaceId = SkyXplorePlanetActions.findEmptySurface(serverPort, accessTokenId, planetId, Constants.SURFACE_TYPE_DESERT);
        SkyXploreConstructionAreaActions.constructConstructionArea(serverPort, accessTokenId, surfaceId, Constants.CONSTRUCTION_AREA_EXTRACTOR);

        SkyXploreGameActions.setPaused(serverPort, accessTokenId, false);

        AwaitilityWrapper.create(120, 5)
            .until(() -> isNull(SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessTokenId, planetId, surfaceId).getConstructionArea().getConstruction()))
            .assertTrue("Construction area construction is not finished.");

        SkyXploreGameActions.setPaused(serverPort, accessTokenId, true);

        return surfaceId;
    }

    private static void cancelConstruction(UUID accessTokenId, UUID planetId, UUID surfaceId) {
        int serverPort = getServerPort();

        UUID constructionId = SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessTokenId, planetId, surfaceId)
            .getConstructionArea()
            .getConstruction()
            .getConstructionId();
        SkyXploreConstructionAreaActions.cancelConstructionAreaConstruction(serverPort, accessTokenId, constructionId);

        AwaitilityWrapper.awaitAssert(
            () -> SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessTokenId, planetId, surfaceId),
            surfaceResponse -> assertThat(surfaceResponse)
                .extracting(SurfaceResponse::getConstructionArea)
                .isNull()
        );
    }

    private static void deconstruct_underConstruction(UUID accessTokenId, UUID planetId, UUID surfaceId) {
        UUID constructionAreaId = SkyXplorePlanetActions.findSurfaceBySurfaceId(getServerPort(), accessTokenId, planetId, surfaceId)
            .getConstructionArea()
            .getConstructionAreaId();
        ResponseValidator.verifyForbiddenOperation(SkyXploreConstructionAreaActions.getDeconstructConstructionAreaResponse(getServerPort(), accessTokenId, constructionAreaId));
    }

    private static UUID construct(UUID accessTokenId, UUID planetId) {
        int serverPort = getServerPort();
        UUID surfaceId = SkyXplorePlanetActions.findEmptySurface(serverPort, accessTokenId, planetId, Constants.SURFACE_TYPE_DESERT);
        SkyXploreConstructionAreaActions.constructConstructionArea(serverPort, accessTokenId, surfaceId, Constants.CONSTRUCTION_AREA_EXTRACTOR);

        AwaitilityWrapper.awaitAssert(
            () -> SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessTokenId, planetId, surfaceId),
            surfaceResponse -> assertThat(surfaceResponse)
                .extracting(SurfaceResponse::getConstructionArea)
                .returns(Constants.CONSTRUCTION_AREA_EXTRACTOR, SurfaceConstructionAreaResponse::getDataId)
                .extracting(SurfaceConstructionAreaResponse::getConstruction)
                .isNotNull()
        );

        return surfaceId;
    }

    private static void construct_terraformationInProgress(UUID accessTokenId, UUID planetId) {
        UUID surfaceId = SkyXplorePlanetActions.findEmptySurface(getServerPort(), accessTokenId, planetId, Constants.SURFACE_TYPE_LAKE);
        SkyXploreSurfaceActions.terraform(getServerPort(), accessTokenId, planetId, surfaceId, Constants.SURFACE_TYPE_FOREST);

        ResponseValidator.verifyErrorResponse(
            SkyXploreConstructionAreaActions.getConstructConstructionAreaResponse(getServerPort(), accessTokenId, surfaceId, Constants.CONSTRUCTION_AREA_EXTRACTOR),
            409,
            ErrorCode.ALREADY_EXISTS
        );
    }

    private static void construct_unsupportedType(UUID accessTokenId, UUID planetId) {
        UUID surfaceId = SkyXplorePlanetActions.findEmptySurface(getServerPort(), accessTokenId, planetId, Constants.SURFACE_TYPE_LAKE);

        ResponseValidator.verifyForbiddenOperation(SkyXploreConstructionAreaActions.getConstructConstructionAreaResponse(getServerPort(), accessTokenId, surfaceId, Constants.CONSTRUCTION_AREA_DEPOT));
    }

    private static void construct_surfaceNotEmpty(UUID accessTokenId, UUID planetId) {
        UUID surfaceId = SkyXplorePlanetActions.findOccupiedSurfaceId(getServerPort(), accessTokenId, planetId);

        ResponseValidator.verifyForbiddenOperation(SkyXploreConstructionAreaActions.getConstructConstructionAreaResponse(getServerPort(), accessTokenId, surfaceId, Constants.CONSTRUCTION_AREA_DEPOT));
    }
}
