package com.github.saphyra.apphub.integration.action.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.Citizen;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.CitizenOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SkyXplorePlanetPopulationOverviewActions {
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
}
