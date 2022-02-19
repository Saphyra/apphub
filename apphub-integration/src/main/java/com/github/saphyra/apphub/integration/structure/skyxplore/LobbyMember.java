package com.github.saphyra.apphub.integration.structure.skyxplore;

import com.github.saphyra.apphub.integration.structure.SelectMenu;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Arrays;

@RequiredArgsConstructor
public class LobbyMember {
    private final WebElement webElement;

    public boolean isReady() {
        return Arrays.asList(webElement.getAttribute("class").split(" "))
            .contains("ready");
    }

    public String getName() {
        return webElement.findElement(By.cssSelector(":scope .lobby-member-name")).getText();
    }

    public void changeAllianceTo(String allianceId) {
        SelectMenu selectMenu = getAllianceSelectionInput();
        selectMenu.selectOption(allianceId);
    }

    private SelectMenu getAllianceSelectionInput() {
        return new SelectMenu(webElement.findElement(By.cssSelector(":scope .lobby-member-alliance-container select")));
    }

    public String getAlliance() {
        return getAllianceSelectionInput()
            .getValue();
    }

    public boolean allianceChangeEnabled() {
        return getAllianceSelectionInput().isEnabled();
    }
}
