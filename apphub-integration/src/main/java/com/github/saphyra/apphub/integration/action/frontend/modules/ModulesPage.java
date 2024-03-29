package com.github.saphyra.apphub.integration.action.frontend.modules;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

class ModulesPage {
    private static final By GET_MODULES = By.cssSelector("#all-modules .module");
    private static final By GET_FAVORITES = By.cssSelector("#favorites .module");
    static final By SEARCH_INPUT = By.id("modules-search-bar");
    private static final By CATEGORIES = By.cssSelector("#all-modules .category");

    static WebElement logoutButton(WebDriver driver) {
        return driver.findElement(By.id("logout-button"));
    }

    static List<WebElement> getModules(WebDriver driver) {
        return driver.findElements(GET_MODULES);
    }

    static List<WebElement> getFavorites(WebDriver driver) {
        return driver.findElements(GET_FAVORITES);
    }

    public static WebElement searchInput(WebDriver driver) {
        return driver.findElement(SEARCH_INPUT);
    }

    public static List<WebElement> getCategories(WebDriver driver) {
        return driver.findElements(CATEGORIES);
    }
}
