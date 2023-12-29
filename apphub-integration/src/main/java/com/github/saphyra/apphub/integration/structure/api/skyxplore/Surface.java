package com.github.saphyra.apphub.integration.structure.api.skyxplore;

import com.github.saphyra.apphub.integration.action.frontend.common.CommonPageActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreModifySurfaceActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
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
        return webElement.getAttribute("id");
    }

    public Optional<String> getBuildingDataId() {
        return WebElementUtils.getClasses(getContent().orElseThrow(() -> new IllegalStateException("Surface content not found.")))
            .stream()
            .filter(s -> s.startsWith(BUILDING_PREFIX))
            .findFirst()
            .map(s -> s.replace(BUILDING_PREFIX, ""));
    }

    public int getBuildingLevel() {
        String result = webElement.findElement(By.className("skyxplore-planet-surface-header-building-level"))
            .getText();
        return Integer.parseInt(result);
    }

    public boolean isConstructionInProgress() {
        return getFooter()
            .filter(footerElement -> !footerElement.findElements(By.cssSelector(":scope .progress-bar-container.construction-progress-bar")).isEmpty())
            .isPresent();
    }

    private Optional<WebElement> getFooter() {
        return getContent()
            .orElseThrow(() -> new IllegalStateException("Surface content not found."))
            .findElements(By.cssSelector(":scope .surface-footer"))
            .stream()
            .findFirst();
    }

    public void cancelConstruction(WebDriver driver) {
        assertThat(isConstructionInProgress()).isTrue();

        getFooter()
            .orElseThrow(() -> new RuntimeException("Surface footer not present."))
            .findElement(By.cssSelector(":scope .cancel-construction-button"))
            .click();

        CommonPageActions.confirmConfirmationDialog(driver, "cancel-construction-confirmation-dialog");

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.isStale(webElement))
            .assertTrue("Planet surface was not reloaded.");
    }

    public boolean canUpgradeBuilding() {
        if (isEmpty()) {
            return false;
        }

        return webElement.findElements(By.cssSelector(":scope .upgrade-building-button"))
            .size() == 1;
    }

    public void upgradeBuilding(WebDriver driver) {
        assertThat(canUpgradeBuilding()).isTrue();

        getFooter()
            .orElseThrow(() -> new RuntimeException("Surface footer not present."))
            .findElement(By.cssSelector(":scope .upgrade-building-button"))
            .click();

        SkyXploreModifySurfaceActions.confirmUpgrade(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.isStale(webElement))
            .assertTrue("Planet surface was not reloaded.");
    }

    public boolean isTerraformationInProgress() {
        return getFooter()
            .filter(footerElement -> !footerElement.findElements(By.cssSelector(":scope .progress-bar-container.terraformation-progress-bar")).isEmpty())
            .isPresent();
    }

    public void cancelTerraformation(WebDriver driver) {
        assertThat(isTerraformationInProgress()).isTrue();

        getFooter()
            .orElseThrow(() -> new RuntimeException("Surface footer not present."))
            .findElement(By.cssSelector(":scope .cancel-terraformation-button"))
            .click();

        CommonPageActions.confirmConfirmationDialog(driver, "cancel-terraformation-confirmation-dialog");

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.isStale(webElement))
            .assertTrue("Planet surface was not reloaded.");
    }

    public void deconstructBuilding(WebDriver driver) {
        webElement.findElement(By.cssSelector(":scope .deconstruct-building-button"))
            .click();

        CommonPageActions.confirmConfirmationDialog(driver, "deconstruct-building-confirmation-dialog");
    }

    public boolean isDeconstructionInProgress() {
        return getFooter()
            .filter(footerElement -> !footerElement.findElements(By.cssSelector(":scope .progress-bar-container.deconstruction-progress-bar")).isEmpty())
            .isPresent();
    }

    public void cancelDeconstruction(WebDriver driver) {
        assertThat(isDeconstructionInProgress()).isTrue();

        getFooter()
            .orElseThrow(() -> new RuntimeException("Surface footer not present."))
            .findElement(By.cssSelector(":scope .cancel-deconstruction-button"))
            .click();

        CommonPageActions.confirmConfirmationDialog(driver, "cancel-deconstruction-confirmation-dialog");

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.isStale(webElement))
            .assertTrue("Planet surface was not reloaded.");
    }
}
