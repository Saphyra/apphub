package com.github.saphyra.apphub.integration.structure.view.skyxplore;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.api.SelectMenu;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@AllArgsConstructor
@Slf4j
public class AiPlayerElement {
    private final WebElement webElement;

    public String getName() {
        return aiNameField()
            .getText();
    }

    private WebElement aiNameField() {
        return webElement.findElement(By.className("skyxplore-lobby-player-name"));
    }

    public void remove() {
        webElement.findElement(By.cssSelector(":scope .skyxplore-lobby-remove-ai-button"))
            .click();
    }

    public void setAlliance(String allianceLabel) {
        SelectMenu selectMenu = new SelectMenu(getAllianceSelect());
        selectMenu.selectOptionByLabel(allianceLabel);
    }

    private WebElement getAllianceSelect() {
        return webElement.findElement(By.cssSelector(":scope .skyxplore-lobby-player-alliance select"));
    }

    public String getAlliance() {
        return getAllianceSelect()
            .findElements(By.cssSelector(":scope option"))
            .stream()
            .filter(WebElement::isSelected)
            .map(WebElement::getText)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No option available"));
    }

    public void rename(String newAiName) {
        int aiNameLength = getName()
            .length();

        aiNameField()
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> aiNameField().getTagName().equalsIgnoreCase("input"))
            .assertTrue("Ai is not editable.");

        WebElementUtils.clearAndFillNotLooseFocus(aiNameField(), newAiName, aiNameLength);

        webElement.click();

        AwaitilityWrapper.createDefault()
            .until(() -> aiNameField().getTagName().equalsIgnoreCase("h4"))
            .assertTrue("Ai is still editable.");
    }
}
