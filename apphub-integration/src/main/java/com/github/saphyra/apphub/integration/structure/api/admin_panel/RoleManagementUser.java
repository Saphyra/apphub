package com.github.saphyra.apphub.integration.structure.api.admin_panel;

import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class RoleManagementUser {
    private static final By AVAILABLE_ROLES = By.cssSelector("td:nth-child(4) button");
    private static final By CURRENT_ROLES = By.cssSelector("td:nth-child(3) button");

    private final WebElement webElement;

    public void addRole(String role) {
        getAvailableRoles()
            .stream()
            .filter(element -> element.getAttribute("id").equalsIgnoreCase(role))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Available role not found: " + role))
            .click();
    }

    private List<WebElement> getAvailableRoles() {
        return webElement.findElements(AVAILABLE_ROLES);
    }

    public List<String> getCurrentRoleNames() {
        return getCurrentRoles()
            .stream()
            .map(element -> element.getAttribute("id"))
            .collect(Collectors.toList());
    }

    public List<String> getAvailableRoleNames() {
        return getAvailableRoles()
            .stream()
            .map(element -> element.getAttribute("id"))
            .collect(Collectors.toList());
    }

    private List<WebElement> getCurrentRoles() {
        return webElement.findElements(CURRENT_ROLES);
    }

    public void removeRole(String role) {
        getCurrentRoles()
            .stream()
            .filter(element -> element.getAttribute("id").equalsIgnoreCase(role))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Current role not found: " + role))
            .click();
    }
}
