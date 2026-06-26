package com.github.saphyra.apphub.integration.action.frontend.monitoring;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.api.monitoring.Feature;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class MonitoringPageActions {
    public static void selectFeature(WebDriver driver, Feature feature) {
        WebElementUtils.selectOptionByValue(driver.findElement(By.id("monitoring-feature")), feature.name());
    }

    public static void selectFunctionality(WebDriver driver, String functionality) {
        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.getSelectOptions(getFunctionalitySelect(driver)).contains(functionality))
            .assertTrue("Functionality not found: " + functionality);

        WebElementUtils.selectOptionByValue(getFunctionalitySelect(driver), functionality);
    }

    private static WebElement getFunctionalitySelect(WebDriver driver) {
        return driver.findElement(By.id("monitoring-functionality"));
    }

    public static List<String> getServices(WebDriver driver) {
        return WebElementUtils.getSelectOptions(driver.findElement(By.id("monitoring-service")));
    }

    public static void load(WebDriver driver) {
        driver.findElement(By.id("monitoring-load-button"))
            .click();
    }

    public static List<String> getMetricServices(WebDriver driver) {
        return driver.findElements(By.className("monitoring-diagram"))
            .stream()
            .map(webElement -> webElement.findElement(By.tagName("legend")).getText())
            .map(text -> text.split(" - ")[2])
            .toList();
    }

    public static void filter(WebDriver driver, String filterText) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("monitoring-filter")), filterText);
    }
}
