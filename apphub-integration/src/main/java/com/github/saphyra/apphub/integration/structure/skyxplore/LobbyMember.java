package com.github.saphyra.apphub.integration.structure.skyxplore;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.SelectMenu;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class LobbyMember {
    private final WebElement webElement;

    public boolean isReady() {
        return Arrays.asList(webElement.getAttribute("class").split(" "))
            .contains("skyxplore-lobby-member-status-ready");
    }

    public String getName() {
        return webElement.findElement(By.cssSelector(":scope .skyxplore-lobby-member-name")).getText();
    }

    public void changeAllianceTo(String allianceName) {
        SelectMenu selectMenu = getAllianceSelectionInput();
        selectMenu.selectOptionByLabel(allianceName);
    }

    private SelectMenu getAllianceSelectionInput() {
        return new SelectMenu(webElement.findElement(By.cssSelector(":scope .skyxplore-lobby-member-alliance select")));
    }

    public String getAlliance() {
        return webElement.findElements(By.cssSelector(":scope .skyxplore-lobby-member-alliance select option"))
            .stream()
            .filter(WebElement::isSelected)
            .map(WebElement::getText)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No option available"));
    }

    public boolean allianceChangeEnabled() {
        return getAllianceSelectionInput().isEnabled();
    }

    public LobbyMemberStatus getStatus() {
        List<String> classes = WebElementUtils.getClasses(webElement);

        return Arrays.stream(LobbyMemberStatus.values())
            .filter(lobbyMemberStatus -> classes.contains(lobbyMemberStatus.getClassName()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("LobbyMember has no recognized status"));
    }
}
