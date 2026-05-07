package com.github.saphyra.apphub.integration.backend.elite_base;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.elite_base.*;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.elite_base.CommodityTradingRequest;
import com.github.saphyra.apphub.integration.structure.api.elite_base.CreateMaterialTraderOverrideRequest;
import com.github.saphyra.apphub.integration.structure.api.elite_base.MaterialType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

public class EliteBaseRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "elite-base", "role-protection"})
    public void eliteBaseRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();


        CommonUtils.verifyMissingRole(() -> EliteBaseNearestActions.getNearestMaterialTradersResponse(getServerPort(), accessToken, UUID.randomUUID(), MaterialType.ENCODED, 0));
        CommonUtils.verifyMissingRole(() -> EliteBaseStarSystemActions.getSearchResponse(getServerPort(), accessToken, null));

        CommonUtils.verifyMissingRole(() -> EliteBasePowerActions.getPowersResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> EliteBasePowerActions.getPowerplayStatesResponse(getServerPort(), accessToken));

        CommonUtils.verifyMissingRole(() -> EliteBaseCommodityTradingActions.getCommoditiesResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> EliteBaseCommodityTradingActions.getBestTradeLocationsResponse(getServerPort(), accessToken, new CommodityTradingRequest()));
        CommonUtils.verifyMissingRole(() -> EliteBaseCommodityTradingActions.getCommodityAveragePriceResponse(getServerPort(), accessToken, "commodityName"));

        CommonUtils.verifyMissingRole(() -> EliteBaseAccountActions.getIsAdminResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> EliteBaseMaterialTraderOverrideActions.getCreateMaterialTraderOverrideResponse(getServerPort(), accessToken, new CreateMaterialTraderOverrideRequest()));
    }

    @Test(dataProvider = "adminRoleProvider", groups = {"be", "elite-base", "role-protection"})
    public void eliteBaseAdminRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ELITE_BASE_ADMIN);
        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);

        SleepUtil.sleep(3000);

        CommonUtils.verifyMissingRole(() -> EliteBaseMaterialTraderOverrideActions.getDeleteMaterialTraderOverrideResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> EliteBaseMaterialTraderOverrideActions.getVerifyMaterialTraderOverrideResponse(getServerPort(), accessToken, UUID.randomUUID()));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_ELITE_BASE},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }

    @DataProvider(parallel = true)
    public Object[][] adminRoleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_ELITE_BASE},
            new Object[]{Constants.ROLE_ACCESS},
            new Object[]{Constants.ROLE_ELITE_BASE_ADMIN}
        };
    }
}
