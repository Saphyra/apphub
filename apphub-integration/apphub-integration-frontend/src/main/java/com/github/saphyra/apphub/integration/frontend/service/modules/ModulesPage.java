package com.github.saphyra.apphub.integration.frontend.service.modules;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

class ModulesPage {
    private static final By LOGOUT_BUTTON = By.id("logout-button");
    private static final By GET_MODULES = By.cssSelector("#all-modules-list .module");
    private static final By GET_FAVORITES = By.cssSelector("#favorites-list .module");
    static final By SEARCH_INPUT = By.id("search-field");
    private static final By CATEGORIES = By.cssSelector("#all-modules-list .category");

    static WebElement logoutButton(WebDriver driver) {
        return driver.findElement(LOGOUT_BUTTON);
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
