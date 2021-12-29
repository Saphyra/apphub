package com.github.saphyra.apphub.integration.frontend.admin_panel.ban;

import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.common.framework.Constants;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.SleepUtil;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.frontend.model.admin_panel.CurrentBan;
import com.github.saphyra.apphub.integration.frontend.model.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.frontend.service.admin_panel.ban.BanActions;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.Future;

public class BanCrudTest extends SeleniumTest {
    private static final String REASON = "reason";

    @Test
    public void banCrud() {
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
        SleepUtil.sleep(3000);
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

        ban_runValidation(adminDriver, "", false, 1, ChronoUnit.MINUTES.name(), REASON, adminUserData.getPassword(), "Válaszd ki a kitiltandó jogosultságot!");
        ban_runValidation(adminDriver, Constants.ROLE_TEST, false, 0, ChronoUnit.MINUTES.name(), REASON, adminUserData.getPassword(), "Időtartam túl kevés (minimum 1)");
        ban_runValidation(adminDriver, Constants.ROLE_TEST, false, 1, "", REASON, adminUserData.getPassword(), "Válassz időegységet!");
        ban_runValidation(adminDriver, Constants.ROLE_TEST, true, 0, "", " ", adminUserData.getPassword(), "A kitiltás oka nem lehet üres.");
        ban_runValidation(adminDriver, Constants.ROLE_TEST, true, 0, "", REASON, "", "A jelszó nem lehet üres.");
        ban_runValidation(adminDriver, Constants.ROLE_TEST, true, 0, "", REASON, "asd", "Hibás jelszó.");

        BanActions.setUpAdminForm(adminDriver, Constants.ROLE_ACCESS, true, 0, "", REASON, adminUserData.getPassword());
        BanActions.submitBanForm(adminDriver);
        NotificationUtil.verifySuccessNotification(adminDriver, "Felhasználó kitiltva.");

        SleepUtil.sleep(3000);
        testDriver.navigate().refresh();
        AwaitilityWrapper.createDefault()
            .until(() -> testDriver.getCurrentUrl().contains(Endpoints.ERROR_PAGE))
            .assertTrue("TestUser is not banned.");

        revokeBan_runValidation(adminDriver, "", "A jelszó nem lehet üres.");
        revokeBan_runValidation(adminDriver, "asd", "Hibás jelszó.");

        revokeBan(adminDriver, adminUserData.getPassword());
        NotificationUtil.verifySuccessNotification(adminDriver, "Tiltás visszavonva.");

        AwaitilityWrapper.createDefault()
            .until(() -> BanActions.getCurrentBans(adminDriver).isEmpty())
            .assertTrue("Ban does not disappear from the list");

        testDriver.navigate()
            .to(UrlFactory.create(Endpoints.MODULES_PAGE));
        AwaitilityWrapper.createDefault()
            .until(() -> testDriver.getCurrentUrl().endsWith(Endpoints.MODULES_PAGE))
            .assertTrue("TestUser is still banned.");
    }

    private void ban_runValidation(WebDriver driver, String bannedRole, boolean permanent, int duration, String chronoUnit, String reason, String password, String errorMessage) {
        BanActions.setUpAdminForm(driver, bannedRole, permanent, duration, chronoUnit, reason, password);
        BanActions.submitBanForm(driver);
        NotificationUtil.verifyErrorNotification(driver, errorMessage);
        NotificationUtil.clearNotifications(driver);
    }

    private void revokeBan_runValidation(WebDriver driver, String password, String errorMessage) {
        revokeBan(driver, password);
        NotificationUtil.verifyErrorNotification(driver, errorMessage);
        NotificationUtil.clearNotifications(driver);
    }

    private void revokeBan(WebDriver driver, String password) {
        CurrentBan currentBan = BanActions.getCurrentBans(driver)
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No ban found"));

        currentBan.revoke(driver, password);
    }
}
