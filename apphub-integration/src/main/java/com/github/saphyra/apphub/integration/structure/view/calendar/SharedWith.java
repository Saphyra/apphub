package com.github.saphyra.apphub.integration.structure.view.calendar;

import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.calendar.Grant;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class SharedWith {
    private final WebElement webElement;

    public String getEmail() {
        return webElement.findElement(By.cssSelector(".shared-with-user-title div:nth-child(2)"))
            .getText();
    }

    public SharedWith toggleGrant(Grant grant) {
        webElement.findElement(By.className("calendar-share-with-selected-user-grants"))
            .findElement(By.cssSelector("input[value='%s']".formatted(grant.name())))
            .click();

        return this;
    }

    public void save(WebDriver driver) {
        webElement.findElement(By.className("shared-with-user-save-button"))
            .click();

        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.CALENDAR_SHARE_GRANTS_SAVED);
    }

    public void unshare(WebDriver driver) {
        webElement.findElement(By.className("shared-with-user-unshare-button"))
            .click();

        driver.findElement(By.id("calendar-share-unshare-confirmation-dialog-unshare-button"))
            .click();
    }
}
