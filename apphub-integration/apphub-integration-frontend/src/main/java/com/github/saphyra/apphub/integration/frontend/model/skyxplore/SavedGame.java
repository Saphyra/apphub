package com.github.saphyra.apphub.integration.frontend.model.skyxplore;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import lombok.Data;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Data
public class SavedGame {
    private final WebElement webElement;

    public void load(WebDriver driver) {
        webElement.click();
        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.SKYXPLORE_LOBBY_PAGE));
    }
}
