package com.github.saphyra.apphub.integration.frontend.admin_panel.ban;

import com.github.saphyra.apphub.integration.action.frontend.RegistrationUtils;
import com.github.saphyra.apphub.integration.action.frontend.admin_panel.ban.BanActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.GenericEndpoints;
import com.github.saphyra.apphub.integration.framework.endpoints.ModulesEndpoints;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BanExpirationTest extends SeleniumTest {
    private static final String REASON = "reason";

    @Test(groups = {"fe", "admin-panel"})
    public void userCanAccessApplicationWhenBanExpired() {
        List<WebDriver> drivers = extractDrivers(2);
        WebDriver testDriver = drivers.get(0);
        WebDriver adminDriver = drivers.get(1);

        RegistrationParameters adminUserData = RegistrationParameters.validParameters();
        RegistrationParameters testUserData = RegistrationParameters.validParameters();

        Integer serverPort = getServerPort();
        RegistrationUtils.registerUsers(serverPort, List.of(new BiWrapper<>(adminDriver, adminUserData), new BiWrapper<>(testDriver, testUserData)));

        DatabaseUtil.addRoleByEmail(adminUserData.getEmail(), Constants.ROLE_ADMIN);
        SleepUtil.sleep(3000);
        adminDriver.navigate().refresh();
        ModulesPageActions.openModule(serverPort, adminDriver, ModuleLocation.BAN);

        BanActions.searchUser(adminDriver, testUserData.getEmail());
        WebElement searchResult = AwaitilityWrapper.getListWithWait(() -> BanActions.getSearchResult(adminDriver), ts -> !ts.isEmpty())
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("TestUser not found."));
        searchResult.click();

        adminDriver.switchTo()
            .window(new ArrayList<>(adminDriver.getWindowHandles()).get(1));

        AwaitilityWrapper.createDefault()
            .until(() -> BanActions.isUserDetailsPageOpened(adminDriver))
            .assertTrue("UserDetails not opened");

        BanActions.setUpAdminForm(adminDriver, Constants.ROLE_ACCESS, false, 1, ChronoUnit.MINUTES.name(), REASON, adminUserData.getPassword());
        BanActions.submitBanForm(adminDriver);

        AwaitilityWrapper.createDefault()
            .until(() -> !BanActions.getCurrentBans(adminDriver).isEmpty())
            .assertTrue("Ban is not created.");

        SleepUtil.sleep(3000);
        testDriver.navigate().refresh();
        AwaitilityWrapper.createDefault()
            .until(() -> testDriver.getCurrentUrl().contains(GenericEndpoints.ERROR_PAGE))
            .assertTrue("TestUser is not banned.");

        AwaitilityWrapper.create(180, 10)
            .until(() -> {
                log.debug("Checking if user is unlocked...");
                testDriver.navigate()
                    .to(UrlFactory.create(serverPort, ModulesEndpoints.MODULES_PAGE));
                return AwaitilityWrapper.create(5, 1)
                    .until(() -> testDriver.getCurrentUrl().endsWith(ModulesEndpoints.MODULES_PAGE))
                    .isResult();
            })
            .assertTrue("TestUser is not unbanned.");
    }
}
