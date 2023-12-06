package com.github.saphyra.apphub.integration.action.frontend.admin_panel.memory_monitoring;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class MemoryMonitoringActions {
    public static List<String> getDisplayedServices(WebDriver driver) {
        return getReportContainers(driver)
            .stream()
            .map(webElement -> webElement.getAttribute("id").split("memory-monitoring-svg-diagram-")[1])
            .collect(Collectors.toList());
    }

    public static List<WebElement> getReportContainers(WebDriver driver) {
        return MemoryMonitoringPage.reportContainers(driver);
    }

    public static void hideAll(WebDriver driver) {
        driver.findElement(By.id("memory-monitoring-hide-all-button"))
            .click();
    }

    public static void showAll(WebDriver driver) {
        driver.findElement(By.id("memory-monitoring-show-all-button"))
            .click();
    }

    public static void toggleService(WebDriver driver, String service) {
        driver.findElement(By.id("memory-monitoring-toggle-%s-button".formatted(service)))
            .click();
    }
}