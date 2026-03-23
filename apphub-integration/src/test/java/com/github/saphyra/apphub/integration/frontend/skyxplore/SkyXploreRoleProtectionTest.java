package com.github.saphyra.apphub.integration.frontend.skyxplore;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreAdminEndpoints;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreDataEndpoints;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreGameEndpoints;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreLobbyEndpoints;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.GameItemType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.UUID;

public class SkyXploreRoleProtectionTest extends SeleniumTest {
    @Test(dataProvider = "roleDataProvider", groups = {"fe", "skyxplore", "role-protection"})
    public void skyXploreRoleProtection(String role) {
        WebDriver driver = extractDriver();

        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);
        SleepUtil.sleep(3000);

        CommonUtils.verifyMissingRole(getServerPort(), driver, SkyXploreDataEndpoints.SKYXPLORE_MAIN_MENU_PAGE);
        CommonUtils.verifyMissingRole(getServerPort(), driver, SkyXploreDataEndpoints.SKYXPLORE_CHARACTER_PAGE);
        CommonUtils.verifyMissingRole(getServerPort(), driver, SkyXploreLobbyEndpoints.SKYXPLORE_LOBBY_PAGE);
        CommonUtils.verifyMissingRole(getServerPort(), driver, SkyXploreGameEndpoints.SKYXPLORE_GAME_PAGE);
    }

    @Test(dataProvider = "adminRoleProvider", groups = {"fe", "skyxplore", "role-protection"})
    public void skyXploreAdminRoleProtection(String role) {
        WebDriver driver = extractDriver();

        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);
        SleepUtil.sleep(3000);

        CommonUtils.verifyMissingRole(getServerPort(), driver, SkyXploreAdminEndpoints.SKYXPLORE_GAME_ADMIN_MAIN_PAGE);
        CommonUtils.verifyMissingRole(driver, UrlFactory.create(getServerPort(), SkyXploreAdminEndpoints.SKYXPLORE_GAME_ADMIN_LIST_PAGE, Map.of("type", GameItemType.GAME, "gameId", UUID.randomUUID())), getServerPort());
        CommonUtils.verifyMissingRole(driver, UrlFactory.create(getServerPort(), SkyXploreAdminEndpoints.SKYXPLORE_GAME_ADMIN_DETAILS_PAGE, Map.of("type", GameItemType.GAME, "gameId", UUID.randomUUID(), "itemId", UUID.randomUUID())), getServerPort());
    }

    @DataProvider(parallel = true)
    public Object[][] roleDataProvider() {
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
