package com.github.saphyra.apphub.integration.action.frontend.admin_panel.ban;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

class BanPage {
    public static WebElement search(WebDriver driver) {
        return driver.findElement(By.id("search"));
    }

    public static WebElement searchButton(WebDriver driver) {
        return driver.findElement(By.id("search-button"));
    }

    public static List<WebElement> searchResultContent(WebDriver driver) {
        return driver.findElements(By.cssSelector("#search-result-content tr"));
    }

    public static WebElement userDetailsPage(WebDriver driver) {
        return driver.findElement(By.id("user-details"));
    }

    public static WebElement bannableRoles(WebDriver driver) {
        return driver.findElement(By.id("bannable-roles"));
    }

    public static WebElement permanentCheckbox(WebDriver driver) {
        return driver.findElement(By.id("permanent"));
    }

    public static WebElement durationInput(WebDriver driver) {
        return driver.findElement(By.id("duration"));
    }

    public static WebElement chronoUnit(WebDriver driver) {
        return driver.findElement(By.id("chrono-unit"));
    }

    public static WebElement reasonTextarea(WebDriver driver) {
        return driver.findElement(By.id("reason"));
    }

    public static WebElement passwordInput(WebDriver driver) {
        return driver.findElement(By.id("password"));
    }

    public static WebElement createBanButton(WebDriver driver) {
        return driver.findElement(By.id("create-ban-button"));
    }

    public static List<WebElement> currentBans(WebDriver driver) {
        return driver.findElements(By.cssSelector("#current-bans tr"));
    }
}
