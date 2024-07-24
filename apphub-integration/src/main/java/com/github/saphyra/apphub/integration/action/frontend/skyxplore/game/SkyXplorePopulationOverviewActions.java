package com.github.saphyra.apphub.integration.action.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkillType;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.citizen.Citizen;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.CitizenOrder;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.citizen.OrderType;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.citizen.StatType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SkyXplorePopulationOverviewActions {
    public static boolean isLoaded(WebDriver driver) {
        return WebElementUtils.isPresent(() -> driver.findElement(By.id("skyxplore-game-population")));
    }

    public static List<Citizen> getCitizens(WebDriver driver) {
        return AwaitilityWrapper.getListWithWait(() -> driver.findElements(By.className("skyxplore-game-population-citizen")), ts -> !ts.isEmpty())
            .stream()
            .map(Citizen::new)
            .collect(Collectors.toList());
    }

    public static Citizen findCitizenValidated(WebDriver driver, String name) {
        return findCitizen(driver, name)
            .orElseThrow(() -> new RuntimeException("Citizen not found with name " + name));
    }

    public static Optional<Citizen> findCitizen(WebDriver driver, String name) {
        return getCitizens(driver)
            .stream()
            .filter(citizen -> citizen.getName().equals(name))
            .findFirst();
    }

    public static void orderCitizens(WebDriver driver, CitizenOrder order) {
        driver.findElement(By.id(order.getId()))
            .click();
    }

    public static void toggleSkillDisplay(WebDriver driver, String skillId) {
        driver.findElement(By.id("skyxplore-game-population-show-and-hide-checkbox-" + skillId.toLowerCase()))
            .click();
    }

    public static int getDisplayedSkillCount(WebDriver driver) {
        return (int) driver.findElements(By.className("skyxplore-game-population-show-and-hide-checkbox"))
            .stream()
            .filter(WebElement::isSelected)
            .count();
    }

    public static void hideAllSkills(WebDriver driver) {
        driver.findElement(By.id("skyxplore-game-population-hide-all-button"))
            .click();
    }

    public static void showAllSkills(WebDriver driver) {
        driver.findElement(By.id("skyxplore-game-population-show-all-button"))
            .click();
    }

    public static void saveHideAsGlobalDefault(WebDriver driver) {
        driver.findElement(By.id("skyxplore-game-population-hide-save-global-default-button"))
            .click();
    }

    public static void saveHideAsPlanetDefault(WebDriver driver) {
        driver.findElement(By.id("skyxplore-game-population-hide-save-planet-default-button"))
            .click();
    }

    public static void close(WebDriver driver) {
        driver.findElement(By.id("skyxplore-game-population-close-button"))
            .click();
    }

    public static boolean isSkillDisplayed(WebDriver driver, String skill) {
        return driver.findElement(By.id("skyxplore-game-population-show-and-hide-checkbox-%s".formatted(skill.toLowerCase())))
            .isSelected();
    }

    public static void deleteHiddenPlanetDefault(WebDriver driver) {
        driver.findElement(By.id("skyxplore-game-population-hide-delete-planet-default-button"))
            .click();
    }

    public static void deleteHiddenGlobalDefault(WebDriver driver) {
        driver.findElement(By.id("skyxplore-game-population-hide-delete-global-default-button"))
            .click();
    }

    public static void orderCitizensBy(WebDriver driver, OrderType orderType) {
        WebElementUtils.selectOption(driver.findElement(By.id("skyxplore-game-population-comparator-selector")), orderType.getValue());
    }

    public static void selectStatType(WebDriver driver, StatType statType) {
        WebElementUtils.selectOption(driver.findElement(By.id("skyxplore-game-population-stat-selector")), statType.name());
    }

    public static void saveOrderAsGlobalDefault(WebDriver driver) {
        driver.findElement(By.id("skyxplore-game-population-order-save-global-default-button"))
            .click();
    }

    public static void selectSkillType(WebDriver driver, SkillType skillType) {
        WebElementUtils.selectOption(driver.findElement(By.id("skyxplore-game-population-skill-selector")), skillType.name());
    }

    public static void saveOrderAsPlanetDefault(WebDriver driver) {
        driver.findElement(By.id("skyxplore-game-population-order-save-planet-default-button"))
            .click();
    }

    public static CitizenOrder getSelectedOrder(WebDriver driver) {
        if (driver.findElement(By.id("skyxplore-game-population-order-ascending")).isSelected()) {
            return CitizenOrder.ASCENDING;
        } else if (driver.findElement(By.id("skyxplore-game-population-order-descending")).isSelected()) {
            return CitizenOrder.DESCENDING;
        } else {
            throw new IllegalStateException("Neither Ascending or Descending order is selected.");
        }
    }

    public static OrderType getOrderType(WebDriver driver) {
        return OrderType.fromValue(driver.findElement(By.id("skyxplore-game-population-comparator-selector")).getAttribute("value"));
    }

    public static SkillType getSelectedSkill(WebDriver driver) {
        return SkillType.valueOf(driver.findElement(By.id("skyxplore-game-population-skill-selector")).getAttribute("value"));
    }

    public static void deleteOrderPlanetDefault(WebDriver driver) {
        driver.findElement(By.id("skyxplore-game-population-order-delete-planet-default-button"))
            .click();
    }

    public static StatType getSelectedStat(WebDriver driver) {
        return StatType.valueOf(driver.findElement(By.id("skyxplore-game-population-stat-selector")).getAttribute("value"));
    }

    public static void deleteOrderGlobalDefault(WebDriver driver) {
        driver.findElement(By.id("skyxplore-game-population-order-delete-global-default-button"))
            .click();
    }
}
