package com.github.saphyra.apphub.integration.frontend.model.skyxplore;

import com.github.saphyra.apphub.integration.frontend.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GameChatMessage {
    private final WebElement webElement;

    public String getFrom() {
        return webElement.findElement(By.cssSelector(":scope .sender-name"))
            .getText();
    }

    public boolean isOwnMessage() {
        return WebElementUtils.getClasses(webElement)
            .contains("own-message");
    }

    public List<String> getMessages() {
        return webElement.findElements(By.cssSelector(":scope .message-list .chat-message"))
            .stream()
            .map(WebElement::getText)
            .collect(Collectors.toList());
    }
}
