package com.github.saphyra.apphub.integration.structure.view.skyxplore;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class BuildingModule {
    private final WebElement webElement;

    public String getDataId() {
        return WebElementUtils.getClasses(webElement)
            .stream()
            .filter(clazz -> clazz.startsWith("skyxplore-game-construction-area-slot-building-"))
            .findFirst()
            .orElseThrow()
            .replace("skyxplore-game-construction-area-slot-building-", "");
    }

    public Boolean isConstructionInProgress() {
        return WebElementUtils.isPresent(() -> webElement.findElement(By.className("skyxplore-game-construction-area-building-cancel-construction-button")));
    }

    public void cancelConstruction() {
        if(!isConstructionInProgress()){
            throw new RuntimeException("Construction is not in progress.");
        }

        webElement.findElement(By.className("skyxplore-game-construction-area-building-cancel-construction-button"))
            .click();
    }

    public void deconstruct(WebDriver driver) {
        webElement.findElement(By.className("skyxplore-game-construction-area-building-module-deconstruct-button"))
            .click();

        driver.findElement(By.id("skyxplore-game-constuction-area-deconstruct-module-confirm-button"))
            .click();
    }

    public Boolean isDeconstructionInProgress() {
        return WebElementUtils.isPresent(() -> webElement.findElement(By.className("skyxplore-game-construction-area-building-cancel-deconstruction-button")));
    }

    public void cancelDeconstruction() {
        webElement.findElement(By.className("skyxplore-game-construction-area-building-cancel-deconstruction-button"))
            .click();
    }
}
