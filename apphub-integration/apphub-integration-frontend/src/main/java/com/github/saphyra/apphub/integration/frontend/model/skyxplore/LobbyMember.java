package com.github.saphyra.apphub.integration.frontend.model.skyxplore;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Arrays;

@RequiredArgsConstructor
public class LobbyMember {
    private final WebElement webElement;

    public boolean isReady() {
        return Arrays.asList(webElement.getAttribute("class").split(" "))
            .contains("ready");
    }

    public String getName() {
        return webElement.findElement(By.cssSelector(":scope .lobby-member-name")).getText();
    }
}
