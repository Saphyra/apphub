package com.github.saphyra.apphub.integration.structure.view.skyxplore;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Optional;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class PlanetBuildingOverviewItem {
    private final WebElement webElement;

    public void toggleDetails() {
        WebElementUtils.getIfPresent(() -> webElement.findElement(By.className("skyxplore-game-planet-overview-tab-item-expand-button")))
            .ifPresent(WebElement::click);
    }

    public Optional<PlanetBuildingOverviewItemDetails> getForDataId(String dataId) {
        return WebElementUtils.getIfPresent(() -> webElement.findElement(By.className("skyxplore-game-planet-overview-building-details-item-" + dataId)))
            .map(row -> PlanetBuildingOverviewItemDetails.builder()
                .totalLevel(Integer.parseInt(row.findElement(By.className("skyxplore-game-planet-overview-building-details-item-total-level")).getText()))
                .usedSlots(Integer.parseInt(row.findElement(By.className("skyxplore-game-planet-overview-building-details-item-used-slots")).getText()))
                .build());
    }

    public int getTotalSots() {
        if (isNull(webElement)) {
            return 0;
        }
        return Integer.parseInt(webElement.findElement(By.className("skyxplore-game-planet-overview-building-total-slots")).getText());
    }
}
