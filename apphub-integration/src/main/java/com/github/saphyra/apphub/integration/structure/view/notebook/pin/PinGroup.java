package com.github.saphyra.apphub.integration.structure.view.notebook.pin;

import com.github.saphyra.apphub.integration.action.frontend.notebook.PinActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class PinGroup {
    @Getter
    private final WebElement webElement;

    public String getName() {
        return webElement.findElement(By.tagName("button"))
            .getText();
    }

    public void open(WebDriver driver) {
        String name = getName();

        webElement.click();

        AwaitilityWrapper.createDefault()
            .until(() -> PinActions.findPinGroupByNameValidated(driver, name).isOpened())
            .assertTrue("PinGroup " + name + " is not opened.");
    }

    public Boolean isOpened() {
        return WebElementUtils.getClasses(webElement.findElement(By.tagName("button")))
            .contains("notebook-selected-pin-group");
    }
}
