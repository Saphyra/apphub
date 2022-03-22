package com.github.saphyra.apphub.integration.action.frontend.admin_panel.memory_monitoring;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class MemoryMonitoringActions {
    public static List<WebElement> getReportContainers(WebDriver driver) {
        return MemoryMonitoringPage.reportContainers(driver);
    }
}
