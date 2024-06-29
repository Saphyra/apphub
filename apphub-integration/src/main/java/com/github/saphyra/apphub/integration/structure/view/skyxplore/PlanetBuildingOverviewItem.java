package com.github.saphyra.apphub.integration.structure.view.skyxplore;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Optional;

@RequiredArgsConstructor
public class PlanetBuildingOverviewItem {
    private final WebElement webElement;

    public void toggleDetails() {
        AwaitilityWrapper.getWithWait(() -> webElement.findElement(By.className("skyxplore-game-planet-overview-tab-item-expand-button")), webElement1 -> true)
            .orElseThrow(() -> new RuntimeException("ToggleButton not found."))
            .click();
    }

    public Optional<PlanetBuildingOverviewItemDetails> getForDataId(String dataId) {
        return WebElementUtils.getIfPresent(() -> webElement.findElement(By.className("skyxplore-game-planet-overview-building-details-item-" + dataId)))
            .map(row -> PlanetBuildingOverviewItemDetails.builder()
                .totalLevel(Integer.parseInt(row.findElement(By.className("skyxplore-game-planet-overview-building-details-item-total-level")).getText()))
                .usedSlots(Integer.parseInt(row.findElement(By.className("skyxplore-game-planet-overview-building-details-item-used-slots")).getText()))
                .build());
    }

    public int getTotalSots() {
        return Integer.parseInt(webElement.findElement(By.className("skyxplore-game-planet-overview-building-total-slots")).getText());
    }

    public int getUsedSlots() {
        return Integer.parseInt(webElement.findElement(By.className("skyxplore-game-planet-overview-building-used-slots")).getText());

    }
}
