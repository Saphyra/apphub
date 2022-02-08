package com.github.saphyra.apphub.integration.structure.modules;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
@Data
public class Favorite {
    private static final By LINK = By.cssSelector(".module-link");
    private static final By FAVORITE_BUTTON = By.cssSelector(".favorite-button");

    private final WebElement element;

    public String getModuleName() {
        return element.findElement(LINK).getText();
    }

    public void removeFromFavorites() {
        element.findElement(FAVORITE_BUTTON).click();
    }
}
