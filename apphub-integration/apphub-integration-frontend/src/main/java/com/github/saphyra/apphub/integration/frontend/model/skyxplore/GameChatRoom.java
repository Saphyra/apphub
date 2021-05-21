package com.github.saphyra.apphub.integration.frontend.model.skyxplore;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class GameChatRoom {
    private final WebElement webElement;

    public String getName() {
        return webElement.findElement(By.cssSelector(":scope span:first-child"))
            .getText();
    }

    public void select() {
        webElement.click();

        AwaitilityWrapper.createDefault()
            .until(this::isSelected)
            .assertTrue("Chat room not selected.");
    }

    private boolean isSelected() {
        return WebElementUtils.getClasses(webElement)
            .contains("active-room-button");
    }
}