package com.github.saphyra.apphub.integration.action.frontend.skyxplore.game;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

class GamePage {
    public static List<WebElement> systemMessages(WebDriver driver) {
        return driver.findElements(By.cssSelector("#general-chat-message-container .system-message"));
    }

    public static List<WebElement> solarSystems(WebDriver driver) {
        return driver.findElements(By.cssSelector("#map-svg-container circle"));
    }

    public static WebElement chatButton(WebDriver driver) {
        return driver.findElement(By.id("chat-button"));
    }

    public static WebElement chat(WebDriver driver) {
        return driver.findElement(By.id("chat"));
    }

    public static List<WebElement> chatRooms(WebDriver driver) {
        return driver.findElements(By.cssSelector("#chat-rooms .chat-button"));
    }

    public static List<WebElement> messages(WebDriver driver) {
        return driver.findElements(By.cssSelector(".chat-message-container"))
            .stream()
            .filter(WebElement::isDisplayed)
            .flatMap(element -> element.findElements(By.cssSelector(":scope .message-sender-container")).stream())
            .collect(Collectors.toList());
    }

    public static WebElement chatInput(WebDriver driver) {
        return driver.findElement(By.id("send-message-input"));
    }

    public static WebElement openCreateChatRoomDialogButton(WebDriver driver) {
        return driver.findElement(By.id("open-create-chat-room-dialog"));
    }

    public static WebElement createChatRoomDialog(WebDriver driver) {
        return driver.findElement(By.id("create-chat-room-container"));
    }

    public static WebElement createChatRoomTitleInput(WebDriver driver) {
        return driver.findElement(By.id("create-chat-room-title-input"));
    }

    public static List<WebElement> playerListForChatRoomCreation(WebDriver driver) {
        return driver.findElements(By.cssSelector("#create-chat-room-players .create-chat-room-player"));
    }

    public static WebElement createChatRoomButton(WebDriver driver) {
        return driver.findElement(By.id("create-chat-room-button"));
    }

    public static WebElement solarSystemPage(WebDriver driver) {
        return driver.findElement(By.id("solar-system"));
    }

    public static List<WebElement> planets(WebDriver driver) {
        return driver.findElements(By.cssSelector("#solar-system-svg-container .planet-svg-element"));
    }

    public static WebElement planetPage(WebDriver driver) {
        return driver.findElement(By.id("planet"));
    }

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

    public static WebElement exitButton(WebDriver driver) {
        return driver.findElement(By.id("exit-button"));
    }

    public static WebElement solarSystemName(WebDriver driver) {
        return driver.findElement(By.id("solar-system-name"));
    }

    public static WebElement solarSystemSvg(WebDriver driver) {
        return driver.findElement(By.id("solar-system-svg-container"));
    }

    public static WebElement closeSolarSystemButton(WebDriver driver) {
        return driver.findElement(By.id("close-solar-system-button"));
    }

    public static WebElement planetName(WebDriver driver) {
        return driver.findElement(By.id("planet-name"));
    }

    public static WebElement planetRightBar(WebDriver driver) {
        return driver.findElement(By.id("planet-right-bar"));
    }

    public static WebElement closePlanetButton(WebDriver driver) {
        return driver.findElement(By.id("close-planet-button"));
    }

    public static List<WebElement> surfacesOfPlanet(WebDriver driver) {
        return driver.findElements(By.cssSelector("#planet-surface-container .surface-table-cell"));
    }

    public static WebElement modifySurfaceWindow(WebDriver driver) {
        return driver.findElement(By.id("modify-surface"));
    }

    public static List<WebElement> availableBuildings(WebDriver driver) {
        return driver.findElements(By.cssSelector("#available-buildings .available-building"));
    }

    public static WebElement terraformationWindow(WebDriver driver) {
        return driver.findElement(By.id("terraformation"));
    }

    public static List<WebElement> terraformingPossibilities(WebDriver driver) {
        return driver.findElements(By.cssSelector(".terraforming-possibility"));
    }

    public static List<WebElement> planetQueue(WebDriver driver) {
        return driver.findElements(By.cssSelector(".queue-item"));
    }

    public static WebElement resumeGameButton(WebDriver driver) {
        return driver.findElement(By.id("resume-game-button"));
    }

    public static WebElement pauseGameButton(WebDriver driver) {
        return driver.findElement(By.id("pause-game-button"));
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
