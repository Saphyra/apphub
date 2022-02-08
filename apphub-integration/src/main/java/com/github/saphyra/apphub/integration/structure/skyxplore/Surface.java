package com.github.saphyra.apphub.integration.structure.skyxplore;

import com.github.saphyra.apphub.integration.action.frontend.common.CommonPageActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreConstructionActions;
import com.github.saphyra.apphub.integration.action.frontend.skyxplore.game.SkyXploreSurfaceActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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
            .orElseThrow(() -> new RuntimeException("SurfaceType is not recognizable"));
    }

    public boolean isEmpty() {
        return WebElementUtils.getClasses(getContent())
            .contains("empty-surface-content");
    }

    public void openConstructNewBuildingWindow(WebDriver driver) {
        assertThat(isEmpty()).isTrue();

        getContent()
            .findElement(By.cssSelector(":scope .surface-footer .empty-surface-construct-new-building-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreConstructionActions.isDisplayed(driver))
            .assertTrue("Construct new building window is not displayed.");
    }

    private WebElement getContent() {
        return webElement.findElement(By.cssSelector(":scope .surface-content"));
    }

    public String getSurfaceId() {
        return webElement.getAttribute("id");
    }

    public String getBuildingDataId() {
        return WebElementUtils.getClasses(getContent())
            .stream()
            .filter(s -> s.startsWith(BUILDING_PREFIX))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Building type not recognizable"))
            .replace(BUILDING_PREFIX, "");
    }

    public int getBuildingLevel() {
        String result = getContent()
            .findElement(By.cssSelector(":scope .surface-header"))
            .getText()
            .split(": ")[1];
        return Integer.parseInt(result);
    }

    public boolean isConstructionInProgress() {
        return getFooter()
            .findElements(By.cssSelector(":scope .progress-bar-container"))
            .size() == 1;

    }

    private WebElement getFooter() {
        return getContent()
            .findElements(By.cssSelector(":scope .surface-footer"))
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Surface footer not present."));
    }

    public void cancelConstruction(WebDriver driver) {
        assertThat(isConstructionInProgress()).isTrue();

        getFooter()
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

    public void upgradeBuilding() {
        assertThat(canUpgradeBuilding()).isTrue();

        getFooter()
            .findElement(By.cssSelector(":scope .upgrade-building-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.isStale(webElement))
            .assertTrue("Planet surface was not reloaded.");
    }

    public void openTerraformationWindow(WebDriver driver) {
        assertThat(isEmpty()).isTrue();

        getContent()
            .findElement(By.cssSelector(":scope .surface-footer .empty-surface-terraform-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXploreSurfaceActions.isDisplayed(driver))
            .assertTrue("Construct new building window is not displayed.");
    }

    public boolean isTerraformationInProgress() {
        return getFooter()
            .findElements(By.cssSelector(":scope .cancel-terraformation-button"))
            .size() == 1;
    }

    public void cancelTerraformation(WebDriver driver) {
        assertThat(isTerraformationInProgress()).isTrue();

        getFooter()
            .findElement(By.cssSelector(":scope .cancel-terraformation-button"))
            .click();

        CommonPageActions.confirmConfirmationDialog(driver, "cancel-terraformation-confirmation-dialog");

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.isStale(webElement))
            .assertTrue("Planet surface was not reloaded.");
    }
}
