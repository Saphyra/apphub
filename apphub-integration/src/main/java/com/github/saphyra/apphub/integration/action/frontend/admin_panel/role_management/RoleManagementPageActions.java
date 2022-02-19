package com.github.saphyra.apphub.integration.action.frontend.admin_panel.role_management;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.structure.admin_panel.RoleManagementUser;
import org.openqa.selenium.WebDriver;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;


public class RoleManagementPageActions {
    public static RoleManagementUser searchForUser(WebDriver driver, String queryString) {
        clearAndFill(RoleManagementPage.searchInput(driver), queryString);

        return AwaitilityWrapper.getListWithWait(
            () -> RoleManagementPage.searchResult(driver),
            ts -> !ts.isEmpty()
        ).stream()
            .map(RoleManagementUser::new)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("User not found by queryString " + queryString));
    }

    public static void back(WebDriver driver) {
        RoleManagementPage.backButton(driver).click();
    }
}
