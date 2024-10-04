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
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.GenericEndpoints;
import com.github.saphyra.apphub.integration.framework.endpoints.ModulesEndpoints;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.LoginParameters;
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
import java.util.ArrayList;
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

        int serverPort = getServerPort();
        RegistrationUtils.registerUsers(serverPort, List.of(new BiWrapper<>(adminDriver, adminUserData), new BiWrapper<>(testDriver, testUserData)));

        DatabaseUtil.addRoleByEmail(adminUserData.getEmail(), Constants.ROLE_ADMIN);
        SleepUtil.sleep(3000);
        adminDriver.navigate().refresh();
        ModulesPageActions.openModule(serverPort, adminDriver, ModuleLocation.BAN);

        openUser(adminDriver, testUserData);

        ban_runValidation(adminDriver, "", false, 1, ChronoUnit.MINUTES.name(), REASON, adminUserData.getPassword(), LocalizedText.BAN_SELECT_ROLE);
        ban_runValidation(adminDriver, Constants.ROLE_TEST, false, 0, ChronoUnit.MINUTES.name(), REASON, adminUserData.getPassword(), LocalizedText.BAN_DURATION_TOO_SHORT);
        ban_runValidation(adminDriver, Constants.ROLE_TEST, false, 1, "", REASON, adminUserData.getPassword(), LocalizedText.BAN_SELECT_TIME_UNIT);
        ban_runValidation(adminDriver, Constants.ROLE_TEST, true, 0, "", " ", adminUserData.getPassword(), LocalizedText.BAN_BLANK_REASON);
        ban_runValidation(adminDriver, Constants.ROLE_TEST, true, 0, "", REASON, "", LocalizedText.EMPTY_PASSWORD);
        ban_runValidation(adminDriver, Constants.ROLE_TEST, true, 0, "", REASON, "asd", LocalizedText.INCORRECT_PASSWORD);
        ban_runValidation(adminDriver, Constants.ROLE_TEST, true, 0, "", REASON, "asd", LocalizedText.INCORRECT_PASSWORD);
        ban_runValidation(adminDriver, Constants.ROLE_TEST, true, 0, "", REASON, "asd", LocalizedText.ACCOUNT_LOCKED);

        AwaitilityWrapper.create(15, 1)
            .until(() -> IndexPageActions.isLoginPageLoaded(serverPort, adminDriver))
            .assertTrue("User is not logged out.");

        DatabaseUtil.unlockUserByEmail(adminUserData.getEmail());
        IndexPageActions.submitLogin(serverPort, adminDriver, LoginParameters.fromRegistrationParameters(adminUserData));
        AwaitilityWrapper.createDefault()
            .until(() -> BanActions.isUserDetailsPageOpened(adminDriver))
            .assertTrue("Ban Details page is not opened.");

        BanActions.setUpAdminForm(adminDriver, Constants.ROLE_ACCESS, true, 0, "", REASON, adminUserData.getPassword());
        BanActions.submitBanForm(adminDriver);

        SleepUtil.sleep(3000);
        testDriver.navigate().refresh();
        AwaitilityWrapper.createDefault()
            .until(() -> testDriver.getCurrentUrl().contains(GenericEndpoints.ERROR_PAGE))
            .assertTrue("TestUser is not banned.");

        new WebDriverWait(testDriver, Duration.ofSeconds(5))
            .until(ExpectedConditions.presenceOfElementLocated(By.id("error-details")));

        BanActions.getCurrentBans(adminDriver)
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No ban found"))
            .revoke();

        revokeBan_runValidation(adminDriver, "", LocalizedText.EMPTY_PASSWORD);
        revokeBan_runValidation(adminDriver, "asd", LocalizedText.INCORRECT_PASSWORD);
        revokeBan_runValidation(adminDriver, "asd", LocalizedText.INCORRECT_PASSWORD);
        revokeBan_runValidation(adminDriver, "asd", LocalizedText.ACCOUNT_LOCKED);

        AwaitilityWrapper.create(15, 1)
            .until(() -> IndexPageActions.isLoginPageLoaded(serverPort, adminDriver))
            .assertTrue("User is not logged out.");

        DatabaseUtil.unlockUserByEmail(adminUserData.getEmail());
        IndexPageActions.submitLogin(serverPort, adminDriver, LoginParameters.fromRegistrationParameters(adminUserData));
        AwaitilityWrapper.createDefault()
            .until(() -> BanActions.isUserDetailsPageOpened(adminDriver))
            .assertTrue("Ban Details page is not opened.");

        revokeBan(adminDriver, adminUserData);

        testDriver.navigate()
            .to(UrlFactory.create(serverPort, ModulesEndpoints.MODULES_PAGE));
        AwaitilityWrapper.createDefault()
            .until(() -> testDriver.getCurrentUrl().endsWith(ModulesEndpoints.MODULES_PAGE))
            .assertTrue("TestUser is still banned.");
    }

    private void openUser(WebDriver adminDriver, RegistrationParameters userData) {
        BanActions.searchUser(adminDriver, userData.getEmail());
        WebElement searchResult = AwaitilityWrapper.getListWithWait(() -> BanActions.getSearchResult(adminDriver), ts -> !ts.isEmpty())
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("TestUser not found."));
        searchResult.click();

        adminDriver.switchTo().window(new ArrayList<>(adminDriver.getWindowHandles()).get(1));

        AwaitilityWrapper.createDefault()
            .until(() -> BanActions.isUserDetailsPageOpened(adminDriver))
            .assertTrue("UserDetails not opened");
    }

    private void ban_runValidation(WebDriver driver, String bannedRole, boolean permanent, int duration, String chronoUnit, String reason, String password, LocalizedText errorMessage) {
        BanActions.setUpAdminForm(driver, bannedRole, permanent, duration, chronoUnit, reason, password);
        BanActions.submitBanForm(driver);
        ToastMessageUtil.verifyErrorToast(driver, errorMessage);
    }

    private void revokeBan_runValidation(WebDriver driver, String password, LocalizedText errorMessage) {
        BanActions.fillRevokeBanPassword(driver, password);
        BanActions.confirmRevoke(driver);

        ToastMessageUtil.verifyErrorToast(driver, errorMessage);
    }

    private static void revokeBan(WebDriver driver, RegistrationParameters userData) {
        BanActions.getCurrentBans(driver)
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No ban found"))
            .revoke();

        BanActions.fillRevokeBanPassword(driver, userData.getPassword());
        BanActions.confirmRevoke(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> BanActions.getCurrentBans(driver).isEmpty())
            .assertTrue("Ban does not disappear from the list");
    }
}
