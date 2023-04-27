package com.github.saphyra.apphub.integration.structure.skyxplore;

import com.github.saphyra.apphub.integration.structure.SelectMenu;
import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@AllArgsConstructor
public class AiPlayerElement {
    private final WebElement webElement;

    public String getName() {
        return webElement.findElement(By.cssSelector(":scope .skyxplore-lobby-member-name"))
            .getText();
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
        return webElement.findElement(By.cssSelector(":scope .skyxplore-lobby-member-alliance select"));
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
}
