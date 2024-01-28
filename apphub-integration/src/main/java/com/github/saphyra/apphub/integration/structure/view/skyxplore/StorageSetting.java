package com.github.saphyra.apphub.integration.structure.view.skyxplore;

import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.RangeInput;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;


@RequiredArgsConstructor
public class StorageSetting {
    private final WebElement webElement;

    public String getResourceName() {
        return webElement.findElement(By.className("skyxplore-game-storage-setting-title"))
            .getText();
    }

    public int getAmount() {
        return Integer.parseInt(getAmountInput().getAttribute("value"));
    }

    private WebElement getAmountInput() {
        return webElement.findElement(By.className("skyxplore-game-storage-setting-amount"));
    }

    public int getPriority() {
        return Integer.parseInt(getPriorityInput().getAttribute("value"));
    }

    private WebElement getPriorityInput() {
        return webElement.findElement(By.className("skyxplore-game-storage-setting-priority"));
    }

    public void setAmount(int amount) {
        clearAndFill(getAmountInput(), String.valueOf(amount));
    }

    public void setPriority(int priority) {
        new RangeInput(getPriorityInput())
            .setValue(priority);
    }

    public void saveChanges(WebDriver driver) {
        webElement.findElement(By.cssSelector(":scope button:first-child")).click();

        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.SKYXPLORE_GAME_STORAGE_SETTING_SAVED);
    }

    public void delete(WebDriver driver) {
        webElement.findElement(By.className("skyxplore-game-storage-setting-delete-button"))
            .click();

        driver.findElement(By.id("skyxplore-game-storage-setting-delete-button"))
                .click();
    }

    public String getDataId() {
        return webElement.getAttribute("id")
            .replace("skyxplore-game-storage-settings-", "");
    }
}
