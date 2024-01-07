package com.github.saphyra.apphub.integration.structure.view.skyxplore;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class GameChatMessage {
    private final WebElement webElement;

    public String getFrom() {
        return getSenderElement()
            .getText();
    }

    private WebElement getSenderElement() {
        return webElement.findElement(By.className("skyxplore-game-chat-message-sender"));
    }

    public boolean isOwnMessage() {
        return WebElementUtils.getClasses(getSenderElement())
            .contains("own-message");
    }

    public String getMessage() {
        return webElement.findElement(By.className("skyxplore-game-chat-message-content"))
            .getText();
    }
}
