package com.github.saphyra.apphub.integration.action.frontend.admin_panel;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.framework.endpoints.UserEndpoints;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.admin_panel.RoleForAllRow;
import io.restassured.response.Response;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class RolesForAllActions {
    public static RoleForAllRow findRoleValidated(WebDriver driver, String role) {
        return new RoleForAllRow(roleByIdSelector(driver, role));
    }

    private static WebElement roleByIdSelector(WebDriver driver, String role) {
        return driver.findElement(By.id("roles-for-all-role-%s".formatted(role.toLowerCase())));
    }

    public static Optional<RoleForAllRow> findRole(WebDriver driver, String role) {
        return WebElementUtils.getIfPresent(() -> roleByIdSelector(driver, role))
            .map(RoleForAllRow::new);
    }

    public static void fillPassword(WebDriver driver, String password) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("roles-for-all-password")), password);
    }

    public static void confirmRevokeFromAll(WebDriver driver) {
        driver.findElement(By.id("roles-for-all-remove-from-all-button"))
            .click();
    }

    public static List<RoleForAllRow> getRoles(WebDriver driver) {
        return driver.findElements(By.className("roles-for-all-role"))
            .stream()
            .map(RoleForAllRow::new)
            .toList();
    }

    public static void confirmAddToAll(WebDriver driver) {
        driver.findElement(By.id("roles-for-all-add-to-all-button"))
            .click();
    }

    public static List<String> getRestrictedRoles(int serverPort, RegistrationParameters registrationParameters) {
        UUID accessToken = IndexPageActions.login(serverPort, registrationParameters.toLoginRequest());

        Response response = RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, UserEndpoints.USER_DATA_ROLES_FOR_ALL_RESTRICTED));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(String[].class));
    }
}
