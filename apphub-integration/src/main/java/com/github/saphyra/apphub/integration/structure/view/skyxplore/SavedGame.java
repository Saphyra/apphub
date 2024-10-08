package com.github.saphyra.apphub.integration.structure.view.skyxplore;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreLobbyEndpoints;
import lombok.Data;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Data
public class SavedGame {
    private final WebElement webElement;

    public void load(WebDriver driver) {
        webElement.click();
        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(SkyXploreLobbyEndpoints.SKYXPLORE_LOBBY_PAGE));
    }
}
