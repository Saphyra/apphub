package com.github.saphyra.apphub.integration.structure.community;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Optional;

@RequiredArgsConstructor
public class GroupMember {
    private final WebElement webElement;

    public String getUsername() {
        return webElement.findElement(By.cssSelector(":scope td:first-child"))
            .getText();
    }

    public String getEmail() {
        return webElement.getAttribute("title");
    }

    public WebElement getCanInviteCheckbox() {
        return webElement.findElement(By.cssSelector(":scope .can-invite-checkbox"));
    }

    public WebElement getCanKickCheckbox() {
        return webElement.findElement(By.cssSelector(":scope .can-kick-checkbox"));
    }

    public WebElement getCanModifyRolesCheckbox() {
        return webElement.findElement(By.cssSelector(":scope .can-modify-roles-checkbox"));
    }

    public WebElement getKickButton() {
        return webElement.findElement(By.cssSelector(":scope .kick-button"));
    }

    public Optional<WebElement> getTransferLeadershipButton() {
        return WebElementUtils.getIfPresent(() -> webElement.findElement(By.cssSelector(":scope .transfer-leadership-button")));
    }
}
