package com.github.saphyra.apphub.integration.frontend.service.admin_panel.role_management;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.model.admin_panel.role_management.RoleManagementUser;
import org.openqa.selenium.WebDriver;

import static com.github.saphyra.apphub.integration.frontend.framework.WebElementUtils.clearAndFill;

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
