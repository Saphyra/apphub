package com.github.saphyra.apphub.integration.structure.view.skyxplore;

import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@AllArgsConstructor
public class OnlineFriend {
    private final WebElement webElement;

    public String getName() {
        return webElement.findElement(By.cssSelector(":scope span"))
            .getText();
    }

    public void invite() {
        webElement.findElement(By.cssSelector(":scope .skyxplore-lobby-active-friend-invite-button"))
            .click();
    }
}
