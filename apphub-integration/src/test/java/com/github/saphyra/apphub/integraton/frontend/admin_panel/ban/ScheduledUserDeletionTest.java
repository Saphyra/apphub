package com.github.saphyra.apphub.integraton.frontend.admin_panel.ban;

import com.github.saphyra.apphub.integration.action.frontend.RegistrationUtils;
import com.github.saphyra.apphub.integration.action.frontend.admin_panel.ban.BanActions;
import com.github.saphyra.apphub.integration.action.frontend.common.CommonPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ScheduledUserDeletionTest extends SeleniumTest {
    private static final LocalDate DATE = LocalDate.now()
        .plusDays(1);
    private static final Integer HOURS = 18;
    private static final Integer MINUTES = 45;
    public static final String USER_DELETION_CONFIRMATION_DIALOG_ID = "user-deletion-confirmation-dialog";

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
        emptyTime(adminDriver);
        emptyPassword(adminDriver);
        incorrectPassword(adminDriver);
        scheduleDeletion(adminDriver, adminUserData);
        deleteSchedule(adminDriver);
    }

    private static void emptyDate(WebDriver adminDriver) {
        BanActions.submitDeleteAccountForm(adminDriver);

        NotificationUtil.verifyErrorNotification(adminDriver, "Date must not be empty.");
    }

    private static void emptyTime(WebDriver adminDriver) {
        BanActions.fillDeleteUserDate(adminDriver, DATE);

        BanActions.submitDeleteAccountForm(adminDriver);

        NotificationUtil.verifyErrorNotification(adminDriver, "Time must not be empty.");
    }

    private static void emptyPassword(WebDriver adminDriver) {
        BanActions.fillDeleteUserTime(adminDriver, HOURS, MINUTES);
        BanActions.submitDeleteAccountForm(adminDriver);

        CommonPageActions.confirmConfirmationDialog(adminDriver, USER_DELETION_CONFIRMATION_DIALOG_ID);

        NotificationUtil.verifyErrorNotification(adminDriver, "Please enter your password.");
        assertThat(CommonPageActions.isConfirmationDialogOpened(adminDriver, USER_DELETION_CONFIRMATION_DIALOG_ID)).isTrue();
    }

    private static void incorrectPassword(WebDriver adminDriver) {
        BanActions.fillDeleteUserPassword(adminDriver, "asd");

        CommonPageActions.confirmConfirmationDialog(adminDriver, USER_DELETION_CONFIRMATION_DIALOG_ID);

        NotificationUtil.verifyErrorNotification(adminDriver, "Incorrect password.");
        assertThat(CommonPageActions.isConfirmationDialogOpened(adminDriver, USER_DELETION_CONFIRMATION_DIALOG_ID)).isTrue();
    }

    private static void scheduleDeletion(WebDriver adminDriver, RegistrationParameters adminUserData) {
        BanActions.fillDeleteUserPassword(adminDriver, adminUserData.getPassword());

        CommonPageActions.confirmConfirmationDialog(adminDriver, USER_DELETION_CONFIRMATION_DIALOG_ID);

        AwaitilityWrapper.createDefault()
            .until(() -> !CommonPageActions.isConfirmationDialogOpened(adminDriver, USER_DELETION_CONFIRMATION_DIALOG_ID))
            .assertTrue("User deletion is not scheduled.");

        assertThat(BanActions.isUserMarkedForDeletion(adminDriver)).isTrue();
        assertThat(BanActions.getUserMarkedForDeletionAt(adminDriver)).isEqualTo(DATE + " " + (HOURS + 2) + ":" + MINUTES + ":00");
    }

    private static void deleteSchedule(WebDriver adminDriver) {
        BanActions.unmarkForDeletion(adminDriver);

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
        AwaitilityWrapper.createDefault()
            .until(() -> BanActions.isUserDetailsPageOpened(adminDriver))
            .assertTrue("UserDetails not opened");
    }
}
