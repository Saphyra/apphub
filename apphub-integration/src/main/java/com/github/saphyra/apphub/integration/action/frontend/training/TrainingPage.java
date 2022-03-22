package com.github.saphyra.apphub.integration.action.frontend.training;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

class TrainingPage {
    public static List<WebElement> menuItems(WebDriver driver) {
        return driver.findElements(By.cssSelector("nav a"));
    }

    public static WebElement chapterHead(WebDriver driver) {
        return driver.findElement(By.id("main-title"));
    }

    public static WebElement nextButton(WebDriver driver) {
        return driver.findElement(By.id("next"));
    }

    public static WebElement previousButton(WebDriver driver) {
        return driver.findElement(By.id("previous"));
    }

    public static WebElement homeButton(WebDriver driver) {
        return driver.findElement(By.id("home"));
    }
}
