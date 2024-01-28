package com.github.saphyra.apphub.integration.action.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.Surface;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreBuildingFlow {
    public static void constructBuilding(WebDriver driver, String surfaceType, String dataId) {
        Surface surface = SkyXplorePlanetActions.findEmptySurface(driver, surfaceType);
        String surfaceId = surface.getSurfaceId();
        surface.openModifySurfaceWindow(driver);

        SkyXploreModifySurfaceActions.constructBuilding(driver, dataId);

        surface = SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId);
        assertThat(surface.isEmpty()).isFalse();
        assertThat(surface.getBuildingDataId()).contains(dataId);
        assertThat(surface.isConstructionInProgress()).isTrue();

        SkyXploreGameActions.resumeGame(driver);

        AwaitilityWrapper.create(180, 5)
            .until(() -> SkyXplorePlanetActions.findBySurfaceId(driver, surfaceId).filter(s -> !s.isConstructionInProgress()).isPresent())
            .assertTrue("Construction is not finished.");

        SkyXploreGameActions.pauseGame(driver);
    }
}
