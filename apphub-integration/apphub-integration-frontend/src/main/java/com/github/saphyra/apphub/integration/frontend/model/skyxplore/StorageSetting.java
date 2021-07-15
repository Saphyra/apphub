package com.github.saphyra.apphub.integration.frontend.model.skyxplore;

import com.github.saphyra.apphub.integration.frontend.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.frontend.model.RangeInput;
import com.github.saphyra.apphub.integration.frontend.service.common.CommonPageActions;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.github.saphyra.apphub.integration.frontend.framework.WebElementUtils.clearAndFill;

@RequiredArgsConstructor
public class StorageSetting {
    private final WebElement webElement;

    public String getResourceName() {
        return webElement.findElement(By.cssSelector(":scope h3")).getText();
    }

    public int getAmount() {
        return Integer.parseInt(getAmountInput().getAttribute("value"));
    }

    private WebElement getAmountInput() {
        return webElement.findElement(By.cssSelector(":scope label:first-child input"));
    }

    public int getBatchSize() {
        return Integer.parseInt(getBatchSizeInput().getAttribute("value"));
    }

    private WebElement getBatchSizeInput() {
        return webElement.findElement(By.cssSelector(":scope label:nth-child(2) input"));
    }

    public int getPriority() {
        return Integer.parseInt(getPriorityInput().getAttribute("value"));
    }

    private WebElement getPriorityInput() {
        return webElement.findElement(By.cssSelector(":scope label:nth-child(3) input"));
    }

    public void setAmount(int amount) {
        clearAndFill(getAmountInput(), String.valueOf(amount));
    }

    public void setBatchSize(int batchSize) {
        clearAndFill(getBatchSizeInput(), String.valueOf(batchSize));
    }

    public void setPriority(int priority) {
        new RangeInput(getPriorityInput())
            .setValue(priority);
    }

    public void saveChanges(WebDriver driver) {
        webElement.findElement(By.cssSelector(":scope button:first-child")).click();

        NotificationUtil.verifySuccessNotification(driver, "Raktár beállítás elmentve.");
    }

    public void delete(WebDriver driver) {
        webElement.findElement(By.cssSelector(":scope button:last-child")).click();

        CommonPageActions.confirmConfirmationDialog(driver, "delete-storage-setting-confirmation-dialog");

        NotificationUtil.verifySuccessNotification(driver, "Raktár beállítás törölve.");
    }
}
