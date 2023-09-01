package com.github.saphyra.apphub.integration.frontend.admin_panel.ban;

import com.github.saphyra.apphub.integration.action.frontend.RegistrationUtils;
import com.github.saphyra.apphub.integration.action.frontend.admin_panel.ban.BanActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.LoginParameters;
import com.github.saphyra.apphub.integration.structure.api.admin_panel.CurrentBan;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BanCrudTest extends SeleniumTest {
    private static final String REASON = "reason";

    @Test(groups = {"fe", "admin-panel"})
    public void banCrud() {
        List<WebDriver> drivers = extractDrivers(2);
        WebDriver testDriver = drivers.get(0);
        WebDriver adminDriver = drivers.get(1);

        RegistrationParameters adminUserData = RegistrationParameters.validParameters();
        RegistrationParameters testUserData = RegistrationParameters.validParameters();

        RegistrationUtils.registerUsers(List.of(new BiWrapper<>(adminDriver, adminUserData), new BiWrapper<>(testDriver, testUserData)));

        DatabaseUtil.addRoleByEmail(adminUserData.getEmail(), Constants.ROLE_ADMIN);
        SleepUtil.sleep(3000);
        adminDriver.navigate().refresh();
        ModulesPageActions.openModule(adminDriver, ModuleLocation.BAN);

        openUser(adminDriver, testUserData);

        ban_runValidation(adminDriver, "", false, 1, ChronoUnit.MINUTES.name(), REASON, adminUserData.getPassword(), "Select role to ban!");
        ban_runValidation(adminDriver, Constants.ROLE_TEST, false, 0, ChronoUnit.MINUTES.name(), REASON, adminUserData.getPassword(), "Duration too low (min. 1)");
        ban_runValidation(adminDriver, Constants.ROLE_TEST, false, 1, "", REASON, adminUserData.getPassword(), "Select time unit!");
        ban_runValidation(adminDriver, Constants.ROLE_TEST, true, 0, "", " ", adminUserData.getPassword(), "Reason must not be blank.");
        ban_runValidation(adminDriver, Constants.ROLE_TEST, true, 0, "", REASON, "", "Password must not be empty");
        ban_runValidation(adminDriver, Constants.ROLE_TEST, true, 0, "", REASON, "asd", "Incorrect password.");
        ban_runValidation(adminDriver, Constants.ROLE_TEST, true, 0, "", REASON, "asd", "Incorrect password.");
        ban_runValidation(adminDriver, Constants.ROLE_TEST, true, 0, "", REASON, "asd", "Account locked. Try again later.");

        DatabaseUtil.unlockUserByEmail(adminUserData.getEmail());
        IndexPageActions.submitLogin(adminDriver, LoginParameters.fromRegistrationParameters(adminUserData));
        AwaitilityWrapper.createDefault()
            .until(() -> adminDriver.getCurrentUrl().endsWith(Endpoints.ADMIN_PANEL_BAN_PAGE))
            .assertTrue("Ban page is not opened.");
        openUser(adminDriver, testUserData);

        BanActions.setUpAdminForm(adminDriver, Constants.ROLE_ACCESS, true, 0, "", REASON, adminUserData.getPassword());
        BanActions.submitBanForm(adminDriver);
        NotificationUtil.verifySuccessNotification(adminDriver, "User banned.");

        SleepUtil.sleep(3000);
        testDriver.navigate().refresh();
        AwaitilityWrapper.createDefault()
            .until(() -> testDriver.getCurrentUrl().contains(Endpoints.ERROR_PAGE))
            .assertTrue("TestUser is not banned.");

        new WebDriverWait(testDriver, Duration.ofSeconds(5))
            .until(ExpectedConditions.presenceOfElementLocated(By.id("message-content")));

        revokeBan_runValidation(adminDriver, "", "Password must not be empty");
        revokeBan_runValidation(adminDriver, "asd", "Incorrect password.");
        revokeBan_runValidation(adminDriver, "asd", "Incorrect password.");
        revokeBan_runValidation(adminDriver, "asd", "Account locked. Try again later.");

        DatabaseUtil.unlockUserByEmail(adminUserData.getEmail());
        IndexPageActions.submitLogin(adminDriver, LoginParameters.fromRegistrationParameters(adminUserData));
        AwaitilityWrapper.createDefault()
            .until(() -> adminDriver.getCurrentUrl().endsWith(Endpoints.ADMIN_PANEL_BAN_PAGE))
            .assertTrue("Ban page is not opened.");
        openUser(adminDriver, testUserData);

        revokeBan(adminDriver, adminUserData.getPassword());
        NotificationUtil.verifySuccessNotification(adminDriver, "Ban revoked.");

        AwaitilityWrapper.createDefault()
            .until(() -> BanActions.getCurrentBans(adminDriver).isEmpty())
            .assertTrue("Ban does not disappear from the list");

        testDriver.navigate()
            .to(UrlFactory.create(Endpoints.MODULES_PAGE));
        AwaitilityWrapper.createDefault()
            .until(() -> testDriver.getCurrentUrl().endsWith(Endpoints.MODULES_PAGE))
            .assertTrue("TestUser is still banned.");
    }

    private void openUser(WebDriver adminDriver, RegistrationParameters userData) {
        BanActions.searchUser(adminDriver, userData.getEmail());
        WebElement searchResult = AwaitilityWrapper.getListWithWait(() -> BanActions.getSearchResult(adminDriver), ts -> !ts.isEmpty())
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("TestUser not found."));
        searchResult.click();
        AwaitilityWrapper.createDefault()
            .until(() -> BanActions.isUserDetailsPageOpened(adminDriver))
            .assertTrue("UserDetails not opened");
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
