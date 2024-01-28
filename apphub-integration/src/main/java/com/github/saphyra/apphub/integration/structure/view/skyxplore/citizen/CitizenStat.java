package com.github.saphyra.apphub.integration.structure.view.skyxplore.citizen;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class CitizenStat {
    private final WebElement webElement;

    public Integer getValue() {
        return Integer.parseInt(webElement.findElement(By.className("skyxplore-game-population-citizen-stat-value")).getText());
    }
}
