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
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);

        SleepUtil.sleep(3000);

        //Terraformation
        CommonUtils.verifyMissingRole(() -> SkyXploreSurfaceActions.getTerraformResponse(getServerPort(), accessTokenId, UUID.randomUUID(), UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> SkyXploreSurfaceActions.getCancelTerraformationResponse(getServerPort(), accessTokenId, UUID.randomUUID(), UUID.randomUUID()));

        //Priority
        CommonUtils.verifyMissingRole(() -> SkyXplorePriorityActions.getUpdatePriorityResponse(getServerPort(), accessTokenId, UUID.randomUUID(), PriorityType.CONSTRUCTION, 2));

        //Storage settings
        CommonUtils.verifyMissingRole(() -> SkyXploreStorageSettingActions.getStorageSettingsResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreStorageSettingActions.getCreateStorageSettingResponse(getServerPort(), accessTokenId, UUID.randomUUID(), new StorageSettingModel()));
        CommonUtils.verifyMissingRole(() -> SkyXploreStorageSettingActions.getDeleteStorageSettingResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreStorageSettingActions.getEditStorageSettingResponse(getServerPort(), accessTokenId, new StorageSettingModel()));

        //Queue
        CommonUtils.verifyMissingRole(() -> SkyXplorePlanetQueueActions.getSetPriorityResponse(getServerPort(), accessTokenId, UUID.randomUUID(), "", UUID.randomUUID(), 3));
        CommonUtils.verifyMissingRole(() -> SkyXplorePlanetQueueActions.getCancelItemResponse(getServerPort(), accessTokenId, UUID.randomUUID(), "", UUID.randomUUID()));

        //Population
        CommonUtils.verifyMissingRole(() -> SkyXplorePopulationActions.getPopulationResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXplorePopulationActions.getRenameCitizenResponse(getServerPort(), accessTokenId, UUID.randomUUID(), ""));

        //Solar system
        CommonUtils.verifyMissingRole(() -> SkyXploreSolarSystemActions.getSolarSystemResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreSolarSystemActions.getRenameSolarSystemResponse(getServerPort(), accessTokenId, UUID.randomUUID(), ""));

        //Planet overview
        CommonUtils.verifyMissingRole(() -> SkyXplorePlanetActions.getPlanetOverviewResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXplorePlanetActions.getRenamePlanetResponse(getServerPort(), accessTokenId, UUID.randomUUID(), ""));

        //Map
        CommonUtils.verifyMissingRole(() -> SkyXploreMapActions.getMapResponse(getServerPort(), accessTokenId));

        //General
        CommonUtils.verifyMissingRole(() -> SkyXploreGameActions.getIsUserInGameResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreGameActions.getExitResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreGameActions.getPauseGameResponse(getServerPort(), accessTokenId, false));
        CommonUtils.verifyMissingRole(() -> SkyXploreGameActions.getIsHostResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreGameActions.getSaveGameResponse(getServerPort(), accessTokenId));

        //Chat
        CommonUtils.verifyMissingRole(() -> SkyXploreGameChatActions.getPlayersResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> SkyXploreGameChatActions.getCreateChatRoomResponse(getServerPort(), accessTokenId, new CreateChatRoomRequest()));
        CommonUtils.verifyMissingRole(() -> SkyXploreGameChatActions.getLeaveChatRoomResponse(getServerPort(), accessTokenId, ""));
        CommonUtils.verifyMissingRole(() -> SkyXploreGameChatActions.getChatRoomsResponse(getServerPort(), accessTokenId));

        //Building
        CommonUtils.verifyMissingRole(() -> SkyXploreBuildingActions.getConstructNewBuildingResponse(getServerPort(), accessTokenId, UUID.randomUUID(), UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> SkyXploreBuildingActions.getUpgradeBuildingResponse(getServerPort(), accessTokenId, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreBuildingActions.getCancelConstructionResponse(getServerPort(), accessTokenId, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreBuildingActions.getDeconstructBuildingResponse(getServerPort(), accessTokenId, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> SkyXploreBuildingActions.getCancelDeconstructionResponse(getServerPort(), accessTokenId, UUID.randomUUID(), UUID.randomUUID()));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_SKYXPLORE},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
