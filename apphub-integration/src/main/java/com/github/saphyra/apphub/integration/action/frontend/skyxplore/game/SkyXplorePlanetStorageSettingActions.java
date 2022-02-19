package com.github.saphyra.apphub.integration.action.frontend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.structure.RangeInput;
import com.github.saphyra.apphub.integration.structure.SelectMenu;
import com.github.saphyra.apphub.integration.structure.skyxplore.StorageSetting;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;
import static org.assertj.core.api.Assertions.assertThat;

public class SkyXplorePlanetStorageSettingActions {
    public static boolean isLoaded(WebDriver driver) {
        return GamePage.storageSettingsWindow(driver).isDisplayed();
    }

    public static void create(WebDriver driver, String resourceId, int amount, int batchSize, int priority) {
        createStorageSettingResourceSelectMenu(driver)
            .selectOption(resourceId);

        clearAndFill(GamePage.createStorageSettingResourceAmountInput(driver), String.valueOf(amount));
        clearAndFill(GamePage.createStorageSettingBatchSizeInput(driver), String.valueOf(batchSize));

        new RangeInput(GamePage.createStorageSettingPriorityInput(driver))
            .setValue(priority);
        assertThat(GamePage.createStorageSettingPriorityLabel(driver).getText()).isEqualTo(String.valueOf(priority));

        GamePage.createStorageSettingButton(driver).click();

        NotificationUtil.verifySuccessNotification(driver, "Raktár beállítás létrehozva.");
    }

    public static SelectMenu createStorageSettingResourceSelectMenu(WebDriver driver) {
        return new SelectMenu(GamePage.createStorageSettingResourceSelectMenu(driver));
    }

    public static List<StorageSetting> getStorageSettings(WebDriver driver) {
        return GamePage.storageSettings(driver)
            .stream()
            .map(StorageSetting::new)
            .collect(Collectors.toList());
    }
}
