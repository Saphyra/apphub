package com.github.saphyra.apphub.integration.structure.modules;

import lombok.Data;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Arrays;

@Data
public class Module {
    private static final By ADD_FAVORITE_BUTTON = By.cssSelector(".favorite-button.non-favorite");
    private static final By LINK = By.cssSelector(".module-link");
    private static final By FAVORITE_BUTTON = By.cssSelector(".favorite-button");
    private static final By REMOVE_FAVORITE_BUTTON = By.cssSelector(".favorite-button.favorite");

    private final WebElement element;
    private final String moduleName;

    public Module(WebElement element) {
        this.element = element;
        moduleName = element.findElement(LINK).getText();
    }

    public void addFavorite() {
        element.findElement(ADD_FAVORITE_BUTTON).click();
    }

    public void removeFavorite() {
        element.findElement(REMOVE_FAVORITE_BUTTON).click();
    }

    public boolean isFavorite() {
        String[] classes = element.findElement(FAVORITE_BUTTON).getAttribute("class").split(" ");
        return Arrays.asList(classes).contains("favorite");
    }

    public String getModuleId() {
        return element.getAttribute("id");
    }

    public void open() {
        element.findElement(LINK).click();
    }
}
