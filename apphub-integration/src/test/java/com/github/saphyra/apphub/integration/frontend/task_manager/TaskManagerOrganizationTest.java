package com.github.saphyra.apphub.integration.frontend.task_manager;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.task_manager.TaskManagementOrganizationPageActions;
import com.github.saphyra.apphub.integration.action.frontend.task_manager.TaskManagerCreateOrganizationPageActions;
import com.github.saphyra.apphub.integration.action.frontend.task_manager.TaskManagerIndexPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.framework.concurrent.FutureWrapper;
import com.github.saphyra.apphub.integration.framework.endpoints.TaskManagerEndpoints;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.task_manager.notification.NotificationType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.task_manager.Invitation;
import com.github.saphyra.apphub.integration.structure.view.task_manager.Notification;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskManagerOrganizationTest extends SeleniumTest {
    private static final String ORGANIZATION_NAME = "organization-name";

    @Test(groups = {"be", "task-manager"})
    public void createAndJoinOrganization() {
        List<WebDriver> driverList = extractDrivers(3);
        WebDriver driver1 = driverList.get(0);
        WebDriver driver2 = driverList.get(1);
        WebDriver driver3 = driverList.get(2);

        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        RegistrationParameters userData3 = RegistrationParameters.validParameters();

        registerUsers(List.of(new BiWrapper<>(userData1, driver1), new BiWrapper<>(userData2, driver2), new BiWrapper<>(userData3, driver3)));

        //Create - Blank title
        TaskManagerIndexPageActions.createOrganization(driver1);
        TaskManagerCreateOrganizationPageActions.fillName(driver1, " ");
        TaskManagerCreateOrganizationPageActions.submit(driver1);
        ToastMessageUtil.verifyErrorToast(driver1, LocalizedText.TASK_MANAGER_ORGANIZATION_NAME_MISSING);

        //Create
        TaskManagerCreateOrganizationPageActions.fillName(driver1, ORGANIZATION_NAME);
        TaskManagerCreateOrganizationPageActions.searchAndInvite(driver1, userData2.getUsername());
        TaskManagerCreateOrganizationPageActions.searchAndInvite(driver1, userData3.getUsername());
        TaskManagerCreateOrganizationPageActions.submit(driver1);

        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(driver1.getCurrentUrl()).endsWith(TaskManagerEndpoints.TASK_MANAGER_PAGE);

            assertThat(TaskManagerIndexPageActions.getOrganizations(driver1))
                .singleElement()
                .returns(ORGANIZATION_NAME, WebElement::getText);
        });

        //Reject invitation
        driver3.navigate().refresh();

        Invitation invitation = AwaitilityWrapper.getListWithWait(() -> TaskManagerIndexPageActions.getInvitations(driver3), invitations -> !invitations.isEmpty())
            .getFirst();
        invitation.reject(driver3);

        AwaitilityWrapper.awaitAssert(() -> assertThat(TaskManagerIndexPageActions.getInvitations(driver3)).isEmpty());

        //Accept invitation
        driver2.navigate().refresh();

        invitation = AwaitilityWrapper.getListWithWait(() -> TaskManagerIndexPageActions.getInvitations(driver2), invitations -> !invitations.isEmpty())
            .getFirst();

        invitation.accept(driver2);

        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(TaskManagerIndexPageActions.getInvitations(driver2)).isEmpty();
            assertThat(TaskManagerIndexPageActions.getOrganizations(driver2)).hasSize(1);
        });

        //Check notifications
        TaskManagerIndexPageActions.getOrganizations(driver1)
            .getFirst()
            .click();

        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(TaskManagementOrganizationPageActions.getUnreadNotificationCount(driver1)).isEqualTo(2);

            List<Notification> notifications = TaskManagementOrganizationPageActions.getNotifications(driver1);
            assertThat(notifications.getFirst().getClasses()).contains(NotificationType.USER_ACCEPTED_YOUR_INVITATION.name());
            assertThat(notifications.get(1).getClasses()).contains(NotificationType.USER_REJECTED_YOUR_INVITATION.name());
        });
    }

    private void registerUsers(List<BiWrapper<RegistrationParameters, WebDriver>> biWrappers) {
        List<FutureWrapper<Void>> futures = biWrappers.stream()
            .map(bw -> EXECUTOR_SERVICE.execute(() -> registerUser(bw.getEntity1(), bw.getEntity2())))
            .toList();

        futures.forEach(f -> f.get().getOrThrow());
    }

    private void registerUser(RegistrationParameters userData, WebDriver driver) {
        Navigation.toIndexPage(getServerPort(), driver);
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.TASK_MANAGER);
    }
}
