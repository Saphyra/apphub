package com.github.saphyra.apphub.integration.structure.view.skyxplore;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.api.SelectMenu;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.LobbyPlayerStatus;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class LobbyPlayer {
    private final WebElement webElement;

    public boolean isReady() {
        return Arrays.asList(webElement.getAttribute("class").split(" "))
            .contains("skyxplore-lobby-player-status-ready");
    }

    public String getName() {
        return webElement.findElement(By.cssSelector(":scope .skyxplore-lobby-player-name")).getText();
    }

    public void changeAllianceTo(String allianceName) {
        SelectMenu selectMenu = getAllianceSelectionInput();
        selectMenu.selectOptionByLabel(allianceName);
    }

    private SelectMenu getAllianceSelectionInput() {
        return new SelectMenu(webElement.findElement(By.cssSelector(":scope .skyxplore-lobby-player-alliance select")));
    }

    public String getAlliance() {
        return webElement.findElements(By.cssSelector(":scope .skyxplore-lobby-player-alliance select option"))
            .stream()
            .filter(WebElement::isSelected)
            .map(WebElement::getText)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No option available"));
    }

    public boolean allianceChangeEnabled() {
        return getAllianceSelectionInput().isEnabled();
    }

    public LobbyPlayerStatus getStatus() {
        List<String> classes = WebElementUtils.getClasses(webElement);

        return Arrays.stream(LobbyPlayerStatus.values())
            .filter(lobbyPlayerStatus -> classes.contains(lobbyPlayerStatus.getClassName()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("LobbyPlayer has no recognized status"));
    }
}
