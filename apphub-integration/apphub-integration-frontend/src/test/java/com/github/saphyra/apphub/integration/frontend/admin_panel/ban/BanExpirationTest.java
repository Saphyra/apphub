package com.github.saphyra.apphub.integration.frontend.admin_panel.ban;

import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.common.framework.Constants;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.frontend.model.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.frontend.service.admin_panel.ban.BanActions;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.Future;

@Slf4j
public class BanExpirationTest extends SeleniumTest {
    private static final String REASON = "reason";

    @Test
    public void userCanAccessApplicationWhenBanExpired() {
        WebDriver testDriver = extractDriver();
        WebDriver adminDriver = extractDriver();

        RegistrationParameters adminUserData = RegistrationParameters.validParameters();
        RegistrationParameters testUserData = RegistrationParameters.validParameters();

        Future<Void> testUserSetup = TestBase.EXECUTOR_SERVICE.submit(() -> {
            Navigation.toIndexPage(testDriver);
            IndexPageActions.registerUser(testDriver, testUserData);
            return null;
        });

        Navigation.toIndexPage(adminDriver);
        IndexPageActions.registerUser(adminDriver, adminUserData);
        DatabaseUtil.addRoleByEmail(adminUserData.getEmail(), Constants.ROLE_ADMIN);
        adminDriver.navigate().refresh();
        ModulesPageActions.openModule(adminDriver, ModuleLocation.BAN);

        AwaitilityWrapper.createDefault()
            .until(testUserSetup::isDone)
            .assertTrue("Test user is not registered.");

        BanActions.searchUser(adminDriver, testUserData.getEmail());
        WebElement searchResult = AwaitilityWrapper.getListWithWait(() -> BanActions.getSearchResult(adminDriver), ts -> !ts.isEmpty())
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("TestUser not found."));
        searchResult.click();
        AwaitilityWrapper.createDefault()
            .until(() -> BanActions.isUserDetailsPageOpened(adminDriver))
            .assertTrue("UserDetails not opened");

        BanActions.setUpAdminForm(adminDriver, Constants.ROLE_ACCESS, false, 1, ChronoUnit.MINUTES.name(), REASON, adminUserData.getPassword());
        BanActions.submitBanForm(adminDriver);
        NotificationUtil.verifySuccessNotification(adminDriver, "Felhasználó kitiltva.");

        testDriver.navigate().refresh();
        AwaitilityWrapper.createDefault()
            .until(() -> testDriver.getCurrentUrl().contains(Endpoints.ERROR_PAGE))
            .assertTrue("TestUser is not banned.");

        AwaitilityWrapper.create(180, 10)
            .until(() -> {
                log.info("Checking is user unlocked...");
                testDriver.navigate()
                    .to(UrlFactory.create(Endpoints.MODULES_PAGE));
                return AwaitilityWrapper.create(5, 1)
                    .until(() -> testDriver.getCurrentUrl().endsWith(Endpoints.MODULES_PAGE))
                    .isResult();
            })
            .assertTrue("TestUser is not unbanned.");
    }
}
