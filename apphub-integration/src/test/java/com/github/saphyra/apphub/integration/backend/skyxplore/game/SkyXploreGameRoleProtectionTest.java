package com.github.saphyra.apphub.integration.backend.skyxplore.game;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreBuildingModuleActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreConstructionAreaActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreGameChatActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreMapActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXplorePlanetQueueActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXplorePopulationActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXplorePriorityActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreStorageSettingActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreSurfaceActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.CreateChatRoomRequest;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PriorityType;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.StorageSettingModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

public class SkyXploreGameRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "skyxplore", "role-protection"})
    public void gameRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        UserDynamoDbRepository.removeRoleByEmail(userData.getEmail(), role);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();

        //Terraformation
        CommonUtils.verifyMissingRole(() -> SkyXploreSurfaceActions.getTerraformResponse(getServerPort(), accessToken, UUID.randomUUID(), UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> SkyXploreSurfaceActions.getCancelTerraformationResponse(getServerPort(), accessToken, UUID.randomUUID(), UUID.randomUUID()));

        //Priority
        CommonUtils.verifyMissingRole(() -> SkyXplorePriorityActions.getUpdatePriorityResponse(getServerPort(), accessToken, UUID.randomUUID(), PriorityType.CONSTRUCTION, 2));

        //Storage settings
        CommonUtils.verifyMissingRole(() -> SkyXploreStorageSettingActions.getStorageSettingsResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreStorageSettingActions.getCreateStorageSettingResponse(getServerPort(), accessToken, UUID.randomUUID(), new StorageSettingModel()));
        CommonUtils.verifyMissingRole(() -> SkyXploreStorageSettingActions.getDeleteStorageSettingResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreStorageSettingActions.getEditStorageSettingResponse(getServerPort(), accessToken, new StorageSettingModel()));

        //Queue
        CommonUtils.verifyMissingRole(() -> SkyXplorePlanetQueueActions.getSetPriorityResponse(getServerPort(), accessToken, UUID.randomUUID(), "", UUID.randomUUID(), 3));
        CommonUtils.verifyMissingRole(() -> SkyXplorePlanetQueueActions.getCancelItemResponse(getServerPort(), accessToken, UUID.randomUUID(), "", UUID.randomUUID()));

        //Population
        CommonUtils.verifyMissingRole(() -> SkyXplorePopulationActions.getPopulationResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXplorePopulationActions.getRenameCitizenResponse(getServerPort(), accessToken, UUID.randomUUID(), ""));

        //Solar system
        CommonUtils.verifyMissingRole(() -> SkyXploreSolarSystemActions.getSolarSystemResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreSolarSystemActions.getRenameSolarSystemResponse(getServerPort(), accessToken, UUID.randomUUID(), ""));

        //Planet overview
        CommonUtils.verifyMissingRole(() -> SkyXplorePlanetActions.getPlanetOverviewResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXplorePlanetActions.getRenamePlanetResponse(getServerPort(), accessToken, UUID.randomUUID(), ""));

        //Map
        CommonUtils.verifyMissingRole(() -> SkyXploreMapActions.getMapResponse(getServerPort(), accessToken));

        //General
        CommonUtils.verifyMissingRole(() -> SkyXploreGameActions.getIsUserInGameResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> SkyXploreGameActions.getExitResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> SkyXploreGameActions.getPauseGameResponse(getServerPort(), accessToken, false));
        CommonUtils.verifyMissingRole(() -> SkyXploreGameActions.getIsHostResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> SkyXploreGameActions.getSaveGameResponse(getServerPort(), accessToken));

        //Chat
        CommonUtils.verifyMissingRole(() -> SkyXploreGameChatActions.getPlayersResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> SkyXploreGameChatActions.getCreateChatRoomResponse(getServerPort(), accessToken, new CreateChatRoomRequest()));
        CommonUtils.verifyMissingRole(() -> SkyXploreGameChatActions.getLeaveChatRoomResponse(getServerPort(), accessToken, ""));
        CommonUtils.verifyMissingRole(() -> SkyXploreGameChatActions.getChatRoomsResponse(getServerPort(), accessToken));

        //Building module
        CommonUtils.verifyMissingRole(() -> SkyXploreBuildingModuleActions.getBuildingModulesResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreBuildingModuleActions.getConstructBuildingModuleResponse(getServerPort(), accessToken, UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> SkyXploreBuildingModuleActions.getCancelConstructionResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreBuildingModuleActions.getDeconstructBuildingModuleResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreBuildingModuleActions.getCancelDeconstructionResponse(getServerPort(), accessToken, UUID.randomUUID()));

        //Construction area
        CommonUtils.verifyMissingRole(() -> SkyXploreConstructionAreaActions.getConstructConstructionAreaResponse(getServerPort(), accessToken, UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> SkyXploreConstructionAreaActions.getCancelConstructionAreaConstructionResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreConstructionAreaActions.getDeconstructConstructionAreaResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreConstructionAreaActions.getCancelDeconstructionOfConstructionAreaResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreConstructionAreaActions.getAvailableBuildingsResponse(getServerPort(), accessToken, UUID.randomUUID(), ""));
    }

    @Test(dataProvider = "adminRoleProvider", groups = {"be", "skyxplore", "role-protection"})
    public void gameAdminRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        UserDynamoDbRepository.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);
        UserDynamoDbRepository.removeRoleByEmail(userData.getEmail(), role);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();

        CommonUtils.verifyMissingRole(() -> SkyXploreGameActions.getProcessTickResponse(getServerPort(), accessToken));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_SKYXPLORE},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }

    @DataProvider(parallel = true)
    public Object[][] adminRoleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_ADMIN},
            new Object[]{Constants.ROLE_SKYXPLORE},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
