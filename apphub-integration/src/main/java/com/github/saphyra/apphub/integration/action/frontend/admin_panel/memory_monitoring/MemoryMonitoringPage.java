package com.github.saphyra.apphub.integration.action.frontend.admin_panel.memory_monitoring;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

class MemoryMonitoringPage {
    public static List<WebElement> reportContainers(WebDriver driver) {
        return driver.findElements(By.className("memory-monitoring-svg-container"));
    }
}
