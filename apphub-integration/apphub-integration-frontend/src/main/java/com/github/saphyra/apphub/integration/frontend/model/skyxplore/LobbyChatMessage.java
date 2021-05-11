package com.github.saphyra.apphub.integration.frontend.model.skyxplore;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LobbyChatMessage {
    private final WebElement webElement;

    public String getSender() {
        return webElement.findElement(By.cssSelector(":scope .sender-name")).getText();
    }

    public boolean isOwn() {
        return Arrays.asList(webElement.getAttribute("class").split(" "))
            .contains("own-message");
    }

    public List<String> getMessages() {
        return webElement.findElements(By.cssSelector(":scope .message-list .chat-message"))
            .stream()
            .map(WebElement::getText)
            .collect(Collectors.toList());
    }
}
