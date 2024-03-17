package com.github.saphyra.apphub.integration.structure.view.admin_panel;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RoleManagementSearchResultUser {
    private final WebElement webElement;

    public String getEmail() {
        return webElement.findElement(By.className("role-management-search-result-user-email"))
            .getText();
    }

    public List<RoleManagementSearchResultUserRole> getAvailableRoles() {
        return webElement.findElements(By.cssSelector(".role-management-search-result-user-available-roles .role-management-role-button"))
            .stream()
            .map(RoleManagementSearchResultUserRole::new)
            .collect(Collectors.toList());
    }

    public List<RoleManagementSearchResultUserRole> getGrantedRoles() {
        return webElement.findElements(By.cssSelector(".role-management-search-result-user-granted-roles .role-management-role-button"))
            .stream()
            .map(RoleManagementSearchResultUserRole::new)
            .collect(Collectors.toList());
    }
}
