package com.github.saphyra.apphub.integration.backend.skyxplore.game.planet.construction;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreBuildingModuleActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreConstructionAreaActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.CustomAssertions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.game.building.BuildingModuleResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class BuildingModuleCrudTest extends BackEndTest {
    @Test(groups = {"be", "skyxplore"})
    public void buildingModuleCrud() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessTokenId, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(getServerPort(), Constants.DEFAULT_GAME_NAME, new Player(accessTokenId, userId1));

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(getServerPort(), accessTokenId)
            .getPlanetId();
        UUID surfaceId = SkyXplorePlanetActions.findEmptySurface(getServerPort(), accessTokenId, planetId, Constants.SURFACE_TYPE_DESERT);
        UUID constructionAreaId = SkyXploreConstructionAreaActions.constructConstructionArea(getServerPort(), accessTokenId, planetId, surfaceId, Constants.CONSTRUCTION_AREA_EXTRACTOR);

        construct_nullDataId(accessTokenId, constructionAreaId);
        construct_noSlotAvailable(accessTokenId, constructionAreaId);
        UUID constructionId = construct(accessTokenId, constructionAreaId);
        deconstruct_underConstruction(accessTokenId, constructionAreaId);
        construct_cancelConstruction(accessTokenId, constructionId);
        construct_finishConstruction(accessTokenId, constructionAreaId);
        UUID deconstructionId = deconstruct(accessTokenId, constructionAreaId);
        deconstruct_alreadyDeconstructed(accessTokenId, constructionAreaId);
        deconstruct_cancelDeconstruction(accessTokenId, deconstructionId);
        deconstruct_finishDeconstruction(accessTokenId, constructionAreaId);
    }

    private static void deconstruct_finishDeconstruction(UUID accessTokenId, UUID constructionAreaId) {
        deconstruct(accessTokenId, constructionAreaId);

        int serverPort = getServerPort();
        SkyXploreGameActions.setPaused(serverPort, accessTokenId, false);

        AwaitilityWrapper.create(120, 1)
            .until(() -> SkyXploreBuildingModuleActions.getBuildingModules(serverPort, accessTokenId, constructionAreaId).isEmpty())
            .assertTrue("BuildingModule is not deconstructed");
    }

    private static void deconstruct_cancelDeconstruction(UUID accessTokenId, UUID deconstructionId) {
        CustomAssertions.singleListAssertThat(SkyXploreBuildingModuleActions.cancelDeconstruction(getServerPort(), accessTokenId, deconstructionId))
            .extracting(BuildingModuleResponse::getDeconstruction)
            .isNull();
    }

    private static void deconstruct_alreadyDeconstructed(UUID accessTokenId, UUID constructionAreaId) {
        UUID buildingModuleId = getBuildingModuleId(accessTokenId, constructionAreaId);

        ResponseValidator.verifyForbiddenOperation(SkyXploreBuildingModuleActions.getDeconstructBuildingModuleResponse(getServerPort(), accessTokenId, buildingModuleId));
    }

    private static UUID deconstruct(UUID accessTokenId, UUID constructionAreaId) {
        UUID buildingModuleId = getBuildingModuleId(accessTokenId, constructionAreaId);

        List<BuildingModuleResponse> modules = SkyXploreBuildingModuleActions.deconstructBuildingModule(getServerPort(), accessTokenId, buildingModuleId);
        CustomAssertions.singleListAssertThat(modules)
            .extracting(BuildingModuleResponse::getDeconstruction)
            .isNotNull();

        return modules.get(0)
            .getDeconstruction()
            .getDeconstructionId();
    }

    private static UUID getBuildingModuleId(UUID accessTokenId, UUID constructionAreaId) {
        return SkyXploreBuildingModuleActions.getBuildingModules(getServerPort(), accessTokenId, constructionAreaId)
            .get(0)
            .getBuildingModuleId();
    }

    private static void construct_finishConstruction(UUID accessTokenId, UUID constructionAreaId) {
        construct(accessTokenId, constructionAreaId);

        int serverPort = getServerPort();
        SkyXploreGameActions.setPaused(serverPort, accessTokenId, false);

        AwaitilityWrapper.create(120, 1)
            .until(() -> isNull(SkyXploreBuildingModuleActions.getBuildingModules(serverPort, accessTokenId, constructionAreaId).get(0).getConstruction()))
            .assertTrue("BuildingModule construction is not finished.");

        SkyXploreGameActions.setPaused(serverPort, accessTokenId, true);
    }

    private static void construct_cancelConstruction(UUID accessTokenId, UUID constructionId) {
        assertThat(SkyXploreBuildingModuleActions.cancelConstruction(getServerPort(), accessTokenId, constructionId)).isEmpty();
    }

    private static void deconstruct_underConstruction(UUID accessTokenId, UUID constructionAreaId) {
        UUID buildingModuleId = getBuildingModuleId(accessTokenId, constructionAreaId);

        ResponseValidator.verifyForbiddenOperation(SkyXploreBuildingModuleActions.getDeconstructBuildingModuleResponse(getServerPort(), accessTokenId, buildingModuleId));
    }

    private static UUID construct(UUID accessTokenId, UUID constructionAreaId) {
        List<BuildingModuleResponse> modules = SkyXploreBuildingModuleActions.constructBuildingModule(getServerPort(), accessTokenId, constructionAreaId, Constants.BUILDING_MODULE_HAMSTER_WHEEL);

        CustomAssertions.singleListAssertThat(modules)
            .extracting(BuildingModuleResponse::getConstruction)
            .isNotNull();


        return modules.get(0)
            .getConstruction()
            .getConstructionId();
    }

    private static void construct_noSlotAvailable(UUID accessTokenId, UUID constructionAreaId) {
        ResponseValidator.verifyForbiddenOperation(SkyXploreBuildingModuleActions.getConstructBuildingModuleResponse(getServerPort(), accessTokenId, constructionAreaId, Constants.BUILDING_MODULE_GARAGE));
    }

    private static void construct_nullDataId(UUID accessTokenId, UUID constructionAreaId) {
        ResponseValidator.verifyInvalidParam(SkyXploreBuildingModuleActions.getConstructBuildingModuleResponse(getServerPort(), accessTokenId, constructionAreaId, null), "buildingModuleDataId", "must not be null");
    }
}
