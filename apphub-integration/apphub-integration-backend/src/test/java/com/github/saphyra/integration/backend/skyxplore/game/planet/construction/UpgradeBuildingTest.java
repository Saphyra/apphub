package com.github.saphyra.integration.backend.skyxplore.game.planet.construction;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.ResponseValidator;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreBuildingActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXplorePlanetStorageActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.ConstructionResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.PlanetStorageResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.Player;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SurfaceBuildingResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SurfaceResponse;
import com.github.saphyra.apphub.integration.common.framework.Constants;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class UpgradeBuildingTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(dataProvider = "languageDataProvider", groups = "skyxplore")
    public void upgradeBuilding(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId, userId1))
            .get(accessTokenId);

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(language, accessTokenId)
            .getPlanetId();

        //Building at max level
        UUID maxLevelBuildingId = findBuilding(language, accessTokenId, planetId, Constants.DATA_ID_BATTERY);

        Response maxLevelResponse = SkyXploreBuildingActions.getUpgradeBuildingResponse(language, accessTokenId, planetId, maxLevelBuildingId);

        ResponseValidator.verifyForbiddenOperation(language, maxLevelResponse);

        //Upgrade building
        UUID upgradableBuildingId = findBuilding(language, accessTokenId, planetId, Constants.DATA_ID_SOLAR_PANEL);

        ConstructionResponse constructionResponse = SkyXploreBuildingActions.upgradeBuilding(language, accessTokenId, planetId, upgradableBuildingId);

        assertThat(constructionResponse.getCurrentWorkPoints()).isEqualTo(0);

        PlanetStorageResponse storageResponse = SkyXplorePlanetStorageActions.getStorageOverview(language, accessTokenId, planetId);

        assertThat(storageResponse.getEnergy().getReservedStorageAmount()).isEqualTo(10);
        assertThat(storageResponse.getBulk().getReservedStorageAmount()).isEqualTo(10);
        assertThat(storageResponse.getLiquid().getReservedStorageAmount()).isEqualTo(10);

        //Construction already in progress
        Response inProgressResponse = SkyXploreBuildingActions.getUpgradeBuildingResponse(language, accessTokenId, planetId, upgradableBuildingId);

        ResponseValidator.verifyErrorResponse(language, inProgressResponse, 409, ErrorCode.ALREADY_EXISTS);

        //Cancel
        SkyXploreBuildingActions.cancelConstruction(language, accessTokenId, planetId, upgradableBuildingId);

        assertThat(findByBuildingId(language, accessTokenId, planetId, upgradableBuildingId).getConstruction()).isNull();
    }

    private SurfaceBuildingResponse findByBuildingId(Language language, UUID accessTokenId, UUID planetId, UUID buildingId) {
        return SkyXplorePlanetActions.getSurfaces(language, accessTokenId, planetId)
            .stream()
            .filter(surfaceResponse -> !isNull(surfaceResponse.getBuilding()))
            .map(SurfaceResponse::getBuilding)
            .filter(surfaceBuildingResponse -> surfaceBuildingResponse.getBuildingId().equals(buildingId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Building not found by buildingId " + buildingId));
    }

    private UUID findBuilding(Language language, UUID accessTokenId, UUID planetId, String dataId) {
        return SkyXplorePlanetActions.getSurfaces(language, accessTokenId, planetId)
            .stream()
            .filter(surfaceResponse -> !isNull(surfaceResponse.getBuilding()))
            .map(SurfaceResponse::getBuilding)
            .filter(surfaceBuildingResponse -> surfaceBuildingResponse.getDataId().equals(dataId))
            .map(SurfaceBuildingResponse::getBuildingId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Building " + dataId + " not found on planet " + planetId));
    }
}
