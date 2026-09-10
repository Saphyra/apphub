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
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
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
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken, characterModel1);
        UUID userId1 = UserDynamoDbRepository.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(getServerPort(), Constants.DEFAULT_GAME_NAME, new Player(accessToken, userId1));

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(getServerPort(), accessToken)
            .getPlanetId();
        UUID surfaceId = SkyXplorePlanetActions.findEmptySurface(getServerPort(), accessToken, planetId, Constants.SURFACE_TYPE_DESERT);
        UUID constructionAreaId = SkyXploreConstructionAreaActions.constructConstructionArea(getServerPort(), accessToken, planetId, surfaceId, Constants.CONSTRUCTION_AREA_EXTRACTOR);

        construct_nullDataId(accessToken, constructionAreaId);
        construct_noSlotAvailable(accessToken, constructionAreaId);
        UUID constructionId = construct(accessToken, constructionAreaId);
        deconstruct_underConstruction(accessToken, constructionAreaId);
        construct_cancelConstruction(accessToken, constructionId);
        construct_finishConstruction(accessToken, constructionAreaId);
        UUID deconstructionId = deconstruct(accessToken, constructionAreaId);
        deconstruct_alreadyDeconstructed(accessToken, constructionAreaId);
        deconstruct_cancelDeconstruction(accessToken, deconstructionId);
        deconstruct_finishDeconstruction(accessToken, constructionAreaId);
    }

    private static void deconstruct_finishDeconstruction(String accessToken, UUID constructionAreaId) {
        deconstruct(accessToken, constructionAreaId);

        int serverPort = getServerPort();
        SkyXploreGameActions.setPaused(serverPort, accessToken, false);

        AwaitilityWrapper.create(120, 1)
            .until(() -> SkyXploreBuildingModuleActions.getBuildingModules(serverPort, accessToken, constructionAreaId).isEmpty())
            .assertTrue("BuildingModule is not deconstructed");
    }

    private static void deconstruct_cancelDeconstruction(String accessToken, UUID deconstructionId) {
        CustomAssertions.singleListAssertThat(SkyXploreBuildingModuleActions.cancelDeconstruction(getServerPort(), accessToken, deconstructionId))
            .extracting(BuildingModuleResponse::getDeconstruction)
            .isNull();
    }

    private static void deconstruct_alreadyDeconstructed(String accessToken, UUID constructionAreaId) {
        UUID buildingModuleId = getBuildingModuleId(accessToken, constructionAreaId);

        ResponseValidator.verifyForbiddenOperation(SkyXploreBuildingModuleActions.getDeconstructBuildingModuleResponse(getServerPort(), accessToken, buildingModuleId));
    }

    private static UUID deconstruct(String accessToken, UUID constructionAreaId) {
        UUID buildingModuleId = getBuildingModuleId(accessToken, constructionAreaId);

        List<BuildingModuleResponse> modules = SkyXploreBuildingModuleActions.deconstructBuildingModule(getServerPort(), accessToken, buildingModuleId);
        CustomAssertions.singleListAssertThat(modules)
            .extracting(BuildingModuleResponse::getDeconstruction)
            .isNotNull();

        return modules.get(0)
            .getDeconstruction()
            .getDeconstructionId();
    }

    private static UUID getBuildingModuleId(String accessToken, UUID constructionAreaId) {
        return SkyXploreBuildingModuleActions.getBuildingModules(getServerPort(), accessToken, constructionAreaId)
            .get(0)
            .getBuildingModuleId();
    }

    private static void construct_finishConstruction(String accessToken, UUID constructionAreaId) {
        construct(accessToken, constructionAreaId);

        int serverPort = getServerPort();
        SkyXploreGameActions.setPaused(serverPort, accessToken, false);

        AwaitilityWrapper.create(120, 1)
            .until(() -> isNull(SkyXploreBuildingModuleActions.getBuildingModules(serverPort, accessToken, constructionAreaId).get(0).getConstruction()))
            .assertTrue("BuildingModule construction is not finished.");

        SkyXploreGameActions.setPaused(serverPort, accessToken, true);
    }

    private static void construct_cancelConstruction(String accessToken, UUID constructionId) {
        assertThat(SkyXploreBuildingModuleActions.cancelConstruction(getServerPort(), accessToken, constructionId)).isEmpty();
    }

    private static void deconstruct_underConstruction(String accessToken, UUID constructionAreaId) {
        UUID buildingModuleId = getBuildingModuleId(accessToken, constructionAreaId);

        ResponseValidator.verifyForbiddenOperation(SkyXploreBuildingModuleActions.getDeconstructBuildingModuleResponse(getServerPort(), accessToken, buildingModuleId));
    }

    private static UUID construct(String accessToken, UUID constructionAreaId) {
        List<BuildingModuleResponse> modules = SkyXploreBuildingModuleActions.constructBuildingModule(getServerPort(), accessToken, constructionAreaId, Constants.BUILDING_MODULE_HAMSTER_WHEEL);

        CustomAssertions.singleListAssertThat(modules)
            .extracting(BuildingModuleResponse::getConstruction)
            .isNotNull();

        return modules.get(0)
            .getConstruction()
            .getConstructionId();
    }

    private static void construct_noSlotAvailable(String accessToken, UUID constructionAreaId) {
        ResponseValidator.verifyForbiddenOperation(SkyXploreBuildingModuleActions.getConstructBuildingModuleResponse(getServerPort(), accessToken, constructionAreaId, Constants.BUILDING_MODULE_GARAGE));
    }

    private static void construct_nullDataId(String accessToken, UUID constructionAreaId) {
        ResponseValidator.verifyInvalidParam(SkyXploreBuildingModuleActions.getConstructBuildingModuleResponse(getServerPort(), accessToken, constructionAreaId, null), "buildingModuleDataId", "must not be null");
    }
}
