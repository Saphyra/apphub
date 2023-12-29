package com.github.saphyra.apphub.integration.action.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.core.ForRemoval;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

@ForRemoval("skyxplore-react")
class GamePage {
    public static WebElement openStorageSettingButton(WebDriver driver) {
        return driver.findElement(By.id("planet-storage-settings-button"));
    }

    public static WebElement storageSettingsWindow(WebDriver driver) {
        return driver.findElement(By.id("storage-settings"));
    }

    public static WebElement createStorageSettingResourceSelectMenu(WebDriver driver) {
        return driver.findElement(By.id("storage-settings-resource-input"));
    }

    public static WebElement createStorageSettingResourceAmountInput(WebDriver driver) {
        return driver.findElement(By.id("storage-settings-amount-input"));
    }

    public static WebElement createStorageSettingPriorityInput(WebDriver driver) {
        return driver.findElement(By.id("storage-settings-priority-input"));
    }

    public static WebElement createStorageSettingPriorityLabel(WebDriver driver) {
        return driver.findElement(By.id("storage-settings-priority-value"));
    }

    public static WebElement createStorageSettingButton(WebDriver driver) {
        return driver.findElement(By.id("create-storage-settings-button"));
    }

    public static List<WebElement> storageSettings(WebDriver driver) {
        return driver.findElements(By.cssSelector("#storage-settings-list .storage-setting"));
    }

    public static WebElement openCitizenOverviewButton(WebDriver driver) {
        return driver.findElement(By.id("planet-open-population-overview-button"));
    }

    public static WebElement populationOverviewWindow(WebDriver driver) {
        return driver.findElement(By.id("population-overview"));
    }

    public static List<WebElement> citizens(WebDriver driver) {
        return driver.findElements(By.cssSelector("#population-overview-citizen-list .population-overview-citizen"));
    }

    public static WebElement populationOverviewOrderContainer(WebDriver driver) {
        return driver.findElement(By.id("population-overview-order-container"));
    }

    public static WebElement openPopulationOverviewOrderMenuButton(WebDriver driver) {
        return driver.findElement(By.id("population-overview-order-toggle-button"));
    }

    public static WebElement orderRadioButton(WebDriver driver, String value) {
        return driver.findElement(By.cssSelector(String.format("[value=\"%s\"].population-overview-order-type", value)));
    }

    public static WebElement populationOverviewSkillDisplayContainer(WebDriver driver) {
        return driver.findElement(By.id("population-overview-skill-selection-container"));
    }

    public static WebElement populationOverviewSkillDisplayButton(WebDriver driver) {
        return driver.findElement(By.id("population-overview-skill-selection-toggle-button"));
    }

    public static WebElement populationOverviewSkillDisplayToggleCheckbox(WebDriver driver, String skillId) {
        return driver.findElement(By.cssSelector(String.format("#population-overview-skill-list input[value=%s]", skillId)));
    }

    public static List<WebElement> populationOverviewSkillDisplayToggleCheckboxes(WebDriver driver) {
        return driver.findElements(By.cssSelector("#population-overview-skill-list input"));
    }

    public static WebElement populationOverviewHideAllSkillsButton(WebDriver driver) {
        return driver.findElement(By.id("population-overview-hide-all-skills"));
    }

    public static WebElement populationOverviewShowAllSkillsButton(WebDriver driver) {
        return driver.findElement(By.id("population-overview-show-all-skills"));
    }

    public static List<WebElement> surfacesOfPlanet(WebDriver driver) {
        return driver.findElements(By.cssSelector("#planet-surface-container .surface-table-cell"));
    }

    public static WebElement terraformationWindow(WebDriver driver) {
        return driver.findElement(By.id("terraformation"));
    }

    public static List<WebElement> terraformingPossibilities(WebDriver driver) {
        return driver.findElements(By.cssSelector(".terraforming-possibility"));
    }

    public static WebElement closeStorageSettingsButton(WebDriver driver) {
        return driver.findElement(By.id("close-storage-settings-button"));
    }

    public static WebElement upgradeBuildingButton(WebDriver driver) {
        return driver.findElement(By.id("upgrade-building-button"));
    }

    public static WebElement upgradeBuildingWindow(WebDriver driver) {
        return driver.findElement(By.id("upgrade-building"));
    }
}
