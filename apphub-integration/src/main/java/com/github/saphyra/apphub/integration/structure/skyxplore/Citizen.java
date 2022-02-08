package com.github.saphyra.apphub.integration.structure.skyxplore;

import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class Citizen {
    private final WebElement webElement;

    public String getName() {
        return getCitizenNameInput().getText();
    }

    private WebElement getCitizenNameInput() {
        return webElement.findElement(By.cssSelector(":scope .population-overview-citizen-name"));
    }

    public void setName(WebDriver driver, String newName) {
        WebElement nameInput = getCitizenNameInput();
        nameInput.click();
        WebElementUtils.clearAndFillContentEditable(driver, nameInput, newName);
        webElement.click();

        NotificationUtil.verifySuccessNotification(driver, "Átnevezés sikeres.");
    }

    public int getDisplayedSkillCount() {
        return webElement.findElements(By.cssSelector(":scope .population-overview-citizen-skills .population-overview-citizen-progress-bar")).size();
    }
}
