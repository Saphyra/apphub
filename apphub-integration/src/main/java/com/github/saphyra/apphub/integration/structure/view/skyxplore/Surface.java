package com.github.saphyra.apphub.integration.structure.view.skyxplore;

import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreModifySurfaceActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
@Slf4j
public class Surface {
    private static final String SURFACE_TYPE_PREFIX = "surface-type-";
    private static final String BUILDING_PREFIX = "building-";

    private final WebElement webElement;

    public String getSurfaceType() {
        return WebElementUtils.getClasses(webElement).stream()
            .filter(s -> s.startsWith(SURFACE_TYPE_PREFIX))
            .findFirst()
            .map(s -> s.replace(SURFACE_TYPE_PREFIX, ""))
            .orElseThrow(() -> new RuntimeException("SurfaceType is not recognizable"))
            .toUpperCase();
    }

    public boolean isEmpty() {
        return getContent()
            .isEmpty();
    }

    public void openModifySurfaceWindow(WebDriver driver) {
        assertThat(isEmpty()).isTrue();

        webElement.findElement(By.className("skyxplore-game-planet-surface-modify-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreModifySurfaceActions.isDisplayed(driver))
            .assertTrue("Construct new building window is not displayed.");
    }

    private Optional<WebElement> getContent() {
        return WebElementUtils.getIfPresent(() -> webElement.findElement(By.className("skyxplore-game-planet-surface-tile-content")));
    }

    public String getSurfaceId() {
        return webElement.getDomAttribute("id");
    }

    public Optional<String> getBuildingDataId() {
        if (isEmpty()) {
            return Optional.empty();
        }

        return WebElementUtils.getClasses(getContent().orElseThrow(() -> new IllegalStateException("Surface content not found.")))
            .stream()
            .filter(s -> s.startsWith(BUILDING_PREFIX))
            .findFirst()
            .map(s -> s.replace(BUILDING_PREFIX, ""));
    }

    public boolean isConstructionInProgress() {
        return WebElementUtils.isPresent(() -> webElement.findElement(By.className("skyxplore-game-planet-surface-footer-cancel-button")));
    }

    public boolean canUpgradeBuilding() {
        return WebElementUtils.isPresent(this::upgradeBuildingButton);
    }

    private WebElement upgradeBuildingButton() {
        return webElement.findElement(By.className("skyxplore-game-planet-surface-building-upgrade-button"));
    }

    public boolean isTerraformationInProgress() {
        return WebElementUtils.isPresent(() -> webElement.findElement(By.className("skyxplore-planet-surface-header-terraformation-target")));
    }

    public void cancelTerraformation(WebDriver driver) {
        assertThat(isTerraformationInProgress()).isTrue();

        webElement.findElement(By.className("skyxplore-game-planet-surface-footer-cancel-button"))
            .click();

        driver.findElement(By.id("skyxplore-game-planet-cancel-terraformation-button"))
            .click();
    }

    public void cancelConstruction(WebDriver driver) {
        webElement.findElement(By.className("skyxplore-game-planet-surface-footer-cancel-button"))
            .click();

        driver.findElement(By.id("skyxplore-game-planet-cancel-construction-area-construction-button"))
            .click();
    }

    public Boolean isIdleConstructionAreaPresent() {
        if (!WebElementUtils.isPresent(() -> webElement.findElement(By.className("skyxplore-game-planet-surface-tile-content-header")))) {
            log.info("Surface tile header not found.");
            return false;
        }

        if (!WebElementUtils.isPresent(() -> webElement.findElement(By.className("skyxplore-game-planet-surface-construction-area-deconstruct-button")))) {
            log.info("Deconstruct button not found.");
            return false;
        }

        if (!WebElementUtils.isPresent(() -> webElement.findElement(By.className("skyxplore-game-planet-surface-construction-area-open-button")))) {
            log.info("Open button not found.");
            return false;
        }

        return true;
    }

    public String getConstructionAreaDataId() {
        WebElement element = webElement.findElement(By.className("skyxplore-game-planet-surface-tile-content"));

        return WebElementUtils.getClasses(element)
            .stream()
            .filter(clazz -> clazz.startsWith("construction-area-"))
            .findFirst()
            .map(clazz -> clazz.replace("construction-area-", ""))
            .orElseThrow();
    }

    public void deconstructConstructionArea(WebDriver driver) {
        webElement.findElement(By.className("skyxplore-game-planet-surface-construction-area-deconstruct-button"))
            .click();

        driver.findElement(By.id("skyxplore-game-planet-surface-confirm-deconstruct-construction-area-button"))
            .click();
    }

    public Boolean isDeconstructionInProgress() {
        return WebElementUtils.isPresent(() -> webElement.findElement(By.className("skyxplore-planet-surface-header-deconstructing")));
    }

    public void cancelDeconstruction(WebDriver driver) {
        webElement.findElement(By.className("skyxplore-game-planet-surface-footer-cancel-button"))
            .click();

        driver.findElement(By.id("skyxplore-game-planet-cancel-deconstruction-button"))
            .click();
    }

    public void openConstructionArea() {
        if(!isIdleConstructionAreaPresent()){
            throw new RuntimeException("ConstructionArea is not present.");
        }

        webElement.findElement(By.className("skyxplore-game-planet-surface-construction-area-open-button"))
            .click();
    }
}
