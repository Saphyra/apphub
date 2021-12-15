package com.github.saphyra.apphub.integration.frontend.service.admin_panel.disabled_roles;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.model.admin_panel.disabled_role_management.DisabledRole;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.integration.frontend.framework.WebElementUtils.clearAndFill;

public class DisabledRolesActions {
    public static void back(WebDriver driver) {
        DisabledRolesPage.back(driver).click();
    }

    public static List<DisabledRole> getDisabledRoles(WebDriver driver) {
        return AwaitilityWrapper.getListWithWait(() -> DisabledRolesPage.disabledRoles(driver), webElements -> !webElements.isEmpty())
            .stream()
            .map(DisabledRole::new)
            .collect(Collectors.toList());
    }

    public static boolean isToggleDisabledRoleConfirmationDialogOpened(WebDriver driver) {
        return DisabledRolesPage.toggleDisabledRoleConfirmationDialog(driver).isDisplayed();
    }

    public static void enterPasswordToDisabledRoleToggleConfirmationDialog(WebDriver driver, String password) {
        clearAndFill(DisabledRolesPage.toggleDisabledRoleConfirmationDialogPassword(driver), password);
    }

    public static void confirmDisabledRoleToggleConfirmationDialog(WebDriver driver) {
        DisabledRolesPage.toggleDisabledRoleConfirmationDialogConfirmButton(driver).click();
    }
}
