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

        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(SkyXplorePlanetActions.findBySurfaceIdValidated(driver, surfaceId))
                .returns(false, Surface::isEmpty)
                .returns(true, Surface::isConstructionInProgress)
                .extracting(surface1 -> surface1.getBuildingDataId().orElseThrow())
                .isEqualTo(dataId);
        });

        SkyXploreGameActions.resumeGame(driver);

        AwaitilityWrapper.create(180, 5)
            .until(() -> SkyXplorePlanetActions.findBySurfaceId(driver, surfaceId).filter(s -> !s.isConstructionInProgress()).isPresent())
            .assertTrue("Construction is not finished.");

        SkyXploreGameActions.pauseGame(driver);
    }
}
