package com.github.saphyra.apphub.integration.frontend.service.admin_panel;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.model.admin_panel.disabled_role_management.DisabledRole;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.integration.frontend.framework.WebElementUtils.clearAndFill;

public class AdminPanelPageActions {
    public static void openRoleManagementPage(WebDriver driver) {
        AdminPanelPage.roleManagementPageLink(driver).click();
    }

    public static void back(WebDriver driver) {
        AdminPanelPage.back(driver).click();
    }

    public static void openDisabledRoleManagementPage(WebDriver driver) {
        AdminPanelPage.disabledRoleManagementPageLink(driver).click();
    }

    public static List<DisabledRole> getDisabledRoles(WebDriver driver) {
        return AwaitilityWrapper.getListWithWait(() -> AdminPanelPage.disabledRoles(driver), webElements -> !webElements.isEmpty())
            .stream()
            .map(DisabledRole::new)
            .collect(Collectors.toList());
    }

    public static boolean isToggleDisabledRoleConfirmationDialogOpened(WebDriver driver) {
        return AdminPanelPage.toggleDisabledRoleConfirmationDialog(driver).isDisplayed();
    }

    public static void enterPasswordToDisabledRoleToggleConfirmationDialog(WebDriver driver, String password) {
        clearAndFill(AdminPanelPage.toggleDisabledRoleConfirmationDialogPassword(driver), password);
    }

    public static void confirmDisabledRoleToggleConfirmationDialog(WebDriver driver) {
        AdminPanelPage.toggleDisabledRoleConfirmationDialogConfirmButton(driver).click();
    }
}
