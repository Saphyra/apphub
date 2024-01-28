package com.github.saphyra.apphub.integration.action.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.api.RangeInput;
import com.github.saphyra.apphub.integration.structure.api.SelectMenu;
import com.github.saphyra.apphub.integration.structure.view.skyxplore.StorageSetting;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;

public class SkyXplorePlanetStorageSettingActions {
    public static boolean isLoaded(WebDriver driver) {
        return WebElementUtils.isPresent(driver, By.id("skyxplore-game-storage-settings"));
    }

    public static void create(WebDriver driver, String resourceId, int amount, int priority) {
        createStorageSettingResourceSelectMenu(driver)
            .selectOptionByValue(resourceId);

        clearAndFill(driver.findElement(By.id("skyxplore-game-storage-setting-creator-amount")), String.valueOf(amount));

        new RangeInput(driver.findElement(By.id("skyxplore-game-storage-setting-creator-priority")))
            .setValue(priority);

        driver.findElement(By.id("skyxplore-game-storage-setting-create-button"))
            .click();
    }

    public static SelectMenu createStorageSettingResourceSelectMenu(WebDriver driver) {
        return new SelectMenu(driver.findElement(By.id("skyxplore-game-storage-setting-creator-commodity")));
    }

    public static List<StorageSetting> getStorageSettings(WebDriver driver) {
        return driver.findElements(By.className("skyxplore-game-storage-setting"))
            .stream()
            .map(StorageSetting::new)
            .collect(Collectors.toList());
    }
}
