package com.github.saphyra.apphub.integration.backend.skyxplore.game;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreBuildingActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreGameChatActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreMapActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePlanetQueueActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePopulationActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePriorityActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreStorageSettingActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSurfaceActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.CreateChatRoomRequest;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PriorityType;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.StorageSettingModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

public class SkyXploreGameRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "skyxplore"})
    public void gameRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);

        SleepUtil.sleep(3000);

        //Terraformation
        CommonUtils.verifyMissingRole(() -> SkyXploreSurfaceActions.getTerraformResponse(accessTokenId, UUID.randomUUID(), UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> SkyXploreSurfaceActions.getCancelTerraformationResponse(accessTokenId, UUID.randomUUID(), UUID.randomUUID()));

        //Priority
        CommonUtils.verifyMissingRole(() -> SkyXplorePriorityActions.getUpdatePriorityResponse(accessTokenId, UUID.randomUUID(), PriorityType.CONSTRUCTION, 2));

        //Storage settings
        CommonUtils.verifyMissingRole(() -> SkyXploreStorageSettingActions.getStorageSettingsResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreStorageSettingActions.getCreateStorageSettingResponse(accessTokenId, UUID.randomUUID(), new StorageSettingModel()));
        CommonUtils.verifyMissingRole(() -> SkyXploreStorageSettingActions.getDeleteStorageSettingResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreStorageSettingActions.getEditStorageSettingResponse(accessTokenId, new StorageSettingModel()));

        //Queue
        CommonUtils.verifyMissingRole(() -> SkyXplorePlanetQueueActions.getSetPriorityResponse(accessTokenId, UUID.randomUUID(), "", UUID.randomUUID(), 3));
        CommonUtils.verifyMissingRole(() -> SkyXplorePlanetQueueActions.getCancelItemResponse(accessTokenId, UUID.randomUUID(), "", UUID.randomUUID()));

        //Population
        CommonUtils.verifyMissingRole(() -> SkyXplorePopulationActions.getPopulationResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXplorePopulationActions.getRenameCitizenResponse(accessTokenId, UUID.randomUUID(), ""));

        //Solar system
        CommonUtils.verifyMissingRole(() -> SkyXploreSolarSystemActions.getSolarSystemResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreSolarSystemActions.getRenameSolarSystemResponse(accessTokenId, UUID.randomUUID(), ""));

        //Planet overview
        CommonUtils.verifyMissingRole(() -> SkyXplorePlanetActions.getPlanetOverviewResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXplorePlanetActions.getRenamePlanetResponse(accessTokenId, UUID.randomUUID(), ""));

        //Map
        CommonUtils.verifyMissingRole(() -> SkyXploreMapActions.getMapResponse(accessTokenId));

        //General
        CommonUtils.verifyMissingRole(() -> SkyXploreGameActions.getIsUserInGameResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreGameActions.getExitResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreGameActions.getPauseGameResponse(accessTokenId, false));
        CommonUtils.verifyMissingRole(() -> SkyXploreGameActions.getIsHostResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreGameActions.getSaveGameResponse(accessTokenId));

        //Chat
        CommonUtils.verifyMissingRole(() -> SkyXploreGameChatActions.getPlayersResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreGameChatActions.getCreateChatRoomResponse(accessTokenId, new CreateChatRoomRequest()));
        CommonUtils.verifyMissingRole(() -> SkyXploreGameChatActions.getLeaveChatRoomResponse(accessTokenId, ""));
        CommonUtils.verifyMissingRole(() -> SkyXploreGameChatActions.getChatRoomsResponse(accessTokenId));

        //Building
        CommonUtils.verifyMissingRole(() -> SkyXploreBuildingActions.getConstructNewBuildingResponse(accessTokenId, UUID.randomUUID(), UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> SkyXploreBuildingActions.getUpgradeBuildingResponse(accessTokenId, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreBuildingActions.getCancelConstructionResponse(accessTokenId, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreBuildingActions.getDeconstructBuildingResponse(accessTokenId, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreBuildingActions.getCancelDeconstructionResponse(accessTokenId, UUID.randomUUID(), UUID.randomUUID()));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_SKYXPLORE},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
