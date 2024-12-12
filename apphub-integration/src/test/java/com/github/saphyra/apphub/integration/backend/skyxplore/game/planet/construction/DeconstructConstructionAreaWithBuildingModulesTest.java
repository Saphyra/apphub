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
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.game.SurfaceConstructionAreaResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.game.building.BuildingModuleResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class DeconstructConstructionAreaWithBuildingModulesTest extends BackEndTest {
    @Test(groups = {"be", "skyxplore"})
    public void deconstructConstructionAreaWithBuildingModulesPresent() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        int serverPort = getServerPort();
        UUID accessTokenId = IndexPageActions.registerAndLogin(serverPort, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(serverPort, accessTokenId, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(serverPort, Constants.DEFAULT_GAME_NAME, new Player(accessTokenId, userId1));

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(serverPort, accessTokenId)
            .getPlanetId();
        UUID surfaceId = SkyXplorePlanetActions.findEmptySurface(serverPort, accessTokenId, planetId, Constants.SURFACE_TYPE_DESERT);
        UUID constructionAreaId = SkyXploreConstructionAreaActions.constructConstructionArea(serverPort, accessTokenId, planetId, surfaceId, Constants.CONSTRUCTION_AREA_EXTRACTOR);
        SkyXploreBuildingModuleActions.constructBuildingModules(serverPort, accessTokenId, constructionAreaId, Constants.BUILDING_MODULE_HAMSTER_WHEEL, Constants.BUILDING_MODULE_SMALL_BATTERY);

        SkyXploreConstructionAreaActions.deconstructConstructionArea(serverPort, accessTokenId, constructionAreaId);

        AwaitilityWrapper.awaitAssert(
            () -> SkyXploreBuildingModuleActions.getBuildingModules(serverPort, accessTokenId, constructionAreaId),
            buildingModuleResponses -> assertThat(buildingModuleResponses)
                .hasSize(2)
                .extracting(BuildingModuleResponse::getDeconstruction)
                .isNotNull()
        );

        AwaitilityWrapper.awaitAssert(
            () -> SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessTokenId, planetId, surfaceId),
            surfaceResponse -> assertThat(surfaceResponse.getConstructionArea())
                .extracting(SurfaceConstructionAreaResponse::getDeconstruction)
                .isNotNull()
        );

        SkyXploreGameActions.setPaused(serverPort, accessTokenId, false);

        AwaitilityWrapper.create(180, 5)
            .until(() -> SkyXploreBuildingModuleActions.getBuildingModules(serverPort, accessTokenId, constructionAreaId).isEmpty())
            .assertTrue("BuildingModules are not deconstructed.");

        assertThat(SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessTokenId, planetId, surfaceId).getConstructionArea())
            .isNotNull();

        AwaitilityWrapper.create(120, 5)
            .until(() -> isNull(SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessTokenId, planetId, surfaceId).getConstructionArea()))
            .assertTrue("ConstructionArea is not deconstructed.");

        SkyXploreGameActions.setPaused(serverPort, accessTokenId, true);
    }
}
