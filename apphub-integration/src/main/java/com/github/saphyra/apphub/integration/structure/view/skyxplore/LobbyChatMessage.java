package com.github.saphyra.apphub.integration.structure.view.skyxplore;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class LobbyChatMessage {
    private final WebElement webElement;

    public String getSender() {
        return webElement.findElement(By.cssSelector(":scope .skyxplore-lobby-message-from"))
            .getText();
    }

    public boolean isOwn() {
        return WebElementUtils.getClasses(webElement)
            .contains("own-message");
    }

    public String getText() {
        return webElement.findElement(By.cssSelector(":scope .skyxplore-lobby-message-content"))
            .getText();
    }
}
