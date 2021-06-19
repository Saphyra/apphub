package com.github.saphyra.apphub.integration.frontend.service.skyxplore.game;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.model.skyxplore.Citizen;
import com.github.saphyra.apphub.integration.frontend.model.skyxplore.CitizenOrder;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class SkyXplorePlanetPopulationOverviewActions {
    public static boolean isLoaded(WebDriver driver) {
        return GamePage.populationOverviewWindow(driver).isDisplayed();
    }

    public static List<Citizen> getCitizens(WebDriver driver) {
        return AwaitilityWrapper.getListWithWait(() -> GamePage.citizens(driver), ts -> !ts.isEmpty())
            .stream()
            .map(Citizen::new)
            .collect(Collectors.toList());
    }

    public static Citizen findCitizen(WebDriver driver, String name) {
        return getCitizens(driver)
            .stream()
            .filter(citizen -> citizen.getName().equals(name))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Citizen not found with name " + name));
    }

    public static void orderCitizens(WebDriver driver, CitizenOrder order) {
        if (!GamePage.populationOverviewOrderContainer(driver).isDisplayed()) {
            GamePage.openPopulationOverviewOrderMenuButton(driver).click();
        }

        GamePage.orderRadioButton(driver, order.getValue()).click();
    }

    public static void toggleSkillDisplay(WebDriver driver, String skillId) {
        if (!GamePage.populationOverviewSkillDisplayContainer(driver).isDisplayed()) {
            GamePage.populationOverviewSkillDisplayButton(driver).click();
        }

        GamePage.populationOverviewSkillDisplayToggleCheckbox(driver, skillId).click();
    }

    public static int getDisplayedSkillCount(WebDriver driver) {
        return (int) GamePage.populationOverviewSkillDisplayToggleCheckboxes(driver)
            .stream()
            .filter(WebElement::isSelected)
            .count();
    }

    public static void hideAllSkills(WebDriver driver) {
        if (!GamePage.populationOverviewSkillDisplayContainer(driver).isDisplayed()) {
            GamePage.populationOverviewSkillDisplayButton(driver).click();
        }

        GamePage.populationOverviewHideAllSkillsButton(driver).click();
    }

    public static void showAllSkills(WebDriver driver) {
        if (!GamePage.populationOverviewSkillDisplayContainer(driver).isDisplayed()) {
            GamePage.populationOverviewSkillDisplayButton(driver).click();
        }

        GamePage.populationOverviewShowAllSkillsButton(driver).click();
    }
}
