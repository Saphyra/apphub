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
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ScheduledUserDeletionTest extends SeleniumTest {
    private static final LocalDate DATE = LocalDate.now()
        .plusDays(1);
    private static final Integer HOURS = 18;
    private static final Integer MINUTES = 45;

    @Test(groups = {"fe", "admin-panel"})
    public void scheduleUserDeletionCd() {
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

        emptyDate(adminDriver);
        emptyPassword(adminDriver);
        incorrectPassword(adminDriver);
        scheduleDeletion(adminDriver, adminUserData);
        deleteSchedule(adminDriver);
    }

    private static void emptyDate(WebDriver adminDriver) {
        BanActions.scheduleForDeletion(adminDriver);

        ToastMessageUtil.verifyErrorToast(adminDriver, LocalizedText.BAN_EMPTY_DATE);
    }

    private static void emptyPassword(WebDriver adminDriver) {
        BanActions.fillDeleteUserTime(adminDriver, DATE, HOURS, MINUTES);
        BanActions.scheduleForDeletion(adminDriver);

        BanActions.confirmScheduleDeletion(adminDriver);

        ToastMessageUtil.verifyErrorToast(adminDriver, LocalizedText.EMPTY_PASSWORD);
    }

    private static void incorrectPassword(WebDriver adminDriver) {
        BanActions.fillDeleteUserPassword(adminDriver, "asd");
        BanActions.confirmAccountDeletion(adminDriver);

        ToastMessageUtil.verifyErrorToast(adminDriver, LocalizedText.INCORRECT_PASSWORD);
    }

    private static void scheduleDeletion(WebDriver adminDriver, RegistrationParameters adminUserData) {
        BanActions.fillDeleteUserPassword(adminDriver, adminUserData.getPassword());
        BanActions.confirmAccountDeletion(adminDriver);

        AwaitilityWrapper.createDefault()
            .until(() -> BanActions.isUserMarkedForDeletion(adminDriver))
            .assertTrue("User is not marked for deletion.");

        assertThat(BanActions.getUserMarkedForDeletionAt(adminDriver)).isEqualTo(DATE + " " + HOURS + ":" + MINUTES);
    }

    private static void deleteSchedule(WebDriver adminDriver) {
        BanActions.unmarkForDeletion(adminDriver);
        BanActions.confirmUnmarkForDeletion(adminDriver);

        AwaitilityWrapper.createDefault()
            .until(() -> !BanActions.isUserMarkedForDeletion(adminDriver))
            .assertTrue("User is not unmarked for deletion.");
    }

    private void openUser(WebDriver adminDriver, RegistrationParameters userData) {
        BanActions.searchUser(adminDriver, userData.getEmail());
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
    }
}
