package com.github.saphyra.apphub.integration.action.frontend.skyxplore.main_menu;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Friend;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.IncomingFriendRequest;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SentFriendRequest;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;


@Slf4j
public class SkyXploreFriendshipActions {
    public static void setUpFriendship(WebDriver driver1, WebDriver driver2, String username1, String username2) {
        log.debug("Setting up friendship between {} and {}", username1, username2);
        AwaitilityWrapper.createDefault()
            .until(() -> driver1.getCurrentUrl().endsWith(Endpoints.SKYXPLORE_MAIN_MENU_PAGE))
            .assertTrue("Lobby page is not opened.");

        AwaitilityWrapper.createDefault()
            .until(() -> driver2.getCurrentUrl().endsWith(Endpoints.SKYXPLORE_MAIN_MENU_PAGE))
            .assertTrue("Lobby page is not opened.");

        fillSearchCharacterForm(driver1, username2);

        findFriendCandidate(driver1, username2)
            .click();

        AwaitilityWrapper.getListWithWait(() -> getIncomingFriendRequests(driver2), incomingFriendRequests -> !incomingFriendRequests.isEmpty())
            .stream()
            .filter(incomingFriendRequest -> incomingFriendRequest.getSenderName().equals(username1))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Incoming friend request not found"))
            .accept();

        AwaitilityWrapper.createDefault()
            .until(() -> !getFriends(driver1).isEmpty())
            .assertTrue("Friend did not appear.");

        AwaitilityWrapper.createDefault()
            .until(() -> !getFriends(driver2).isEmpty())
            .assertTrue("Friend did not appear.");
    }

    public static WebElement findFriendCandidate(WebDriver driver, String username) {
        return AwaitilityWrapper.getListWithWait(() -> getFriendCandidates(driver), webElements -> !webElements.isEmpty())
            .stream()
            .filter(element -> element.getText().equals(username))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Friend candidate not found"));
    }

    public static void fillSearchCharacterForm(WebDriver driver, String username) {
        clearAndFill(MainMenuPage.newFriendName(driver), username);
    }

    public static List<Friend> getFriends(WebDriver driver) {
        return MainMenuPage.friends(driver)
            .stream()
            .map(Friend::new)
            .collect(Collectors.toList());
    }

    public static List<WebElement> getFriendCandidates(WebDriver driver) {
        return MainMenuPage.friendCandidates(driver);
    }

    public static List<IncomingFriendRequest> getIncomingFriendRequests(WebDriver driver) {
        return MainMenuPage.incomingFriendRequests(driver)
            .stream()
            .map(IncomingFriendRequest::new)
            .collect(Collectors.toList());
    }

    public static List<SentFriendRequest> getSentFriendRequests(WebDriver driver) {
        return MainMenuPage.sentFriendRequests(driver)
            .stream()
            .map(SentFriendRequest::new)
            .collect(Collectors.toList());
    }
}
