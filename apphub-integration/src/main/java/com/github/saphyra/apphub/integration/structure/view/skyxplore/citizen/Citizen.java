package com.github.saphyra.apphub.integration.structure.view.skyxplore.citizen;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class Citizen {
    private final WebElement webElement;

    public String getName() {
        if (editingEnabled()) {
            return getCitizenNameInput()
                .getAttribute("value");
        } else {
            return webElement.findElement(By.className("skyxplore-game-population-citizen-name"))
                .getText();
        }
    }

    private boolean editingEnabled() {
        return WebElementUtils.isPresent(this::getCitizenNameInput);
    }

    private WebElement getCitizenNameInput() {
        return webElement.findElement(By.className("skyxplore-game-population-citizen-name-input"));
    }

    public void setName(String newName) {
        if (!editingEnabled()) {
            enableEditing();
        }
        WebElement nameInput = getCitizenNameInput();
        WebElementUtils.clearAndFill(nameInput, newName);

        webElement.findElement(By.className("skyxplore-game-population-citizen-name-save-button"))
            .click();
    }

    private void enableEditing() {
        webElement.findElement(By.className("skyxplore-game-population-citizen-rename-button"))
            .click();
    }

    public int getDisplayedSkillCount() {
        return webElement.findElements(By.className("skyxplore-game-population-citizen-status-bar-bar")).size();
    }

    public CitizenStat getStat(String statName) {
        return new CitizenStat(webElement.findElement(By.className("skyxplore-game-population-citizen-status-bar-" + statName)));
    }
}
