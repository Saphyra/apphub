package com.github.saphyra.apphub.integration.frontend.service.skyxplore.main_menu;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.frontend.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.frontend.model.skyxplore.Friend;
import com.github.saphyra.apphub.integration.frontend.model.skyxplore.IncomingFriendRequest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.integration.frontend.framework.WebElementUtils.clearAndFill;

public class SkyXploreFriendshipActions {
    public static void setUpFriendship(WebDriver driver1, WebDriver driver2, String username1, String username2) {
        AwaitilityWrapper.createDefault()
            .until(() -> driver1.getCurrentUrl().endsWith(Endpoints.SKYXPLORE_MAIN_MENU_PAGE))
            .assertTrue("Lobby page is not opened.");

        AwaitilityWrapper.createDefault()
            .until(() -> driver2.getCurrentUrl().endsWith(Endpoints.SKYXPLORE_MAIN_MENU_PAGE))
            .assertTrue("Lobby page is not opened.");

        clearAndFill(MainMenuPage.newFriendName(driver1), username2);

        AwaitilityWrapper.getListWithWait(() -> getFriendCandidates(driver1), webElements -> !webElements.isEmpty())
            .stream()
            .filter(element -> element.getText().equals(username2))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Friend candidate not found"))
            .click();
        NotificationUtil.verifySuccessNotification(driver1, "Barátkérelem elküldve.");

        AwaitilityWrapper.getListWithWait(() -> getIncomingFriendRequests(driver2), incomingFriendRequests -> !incomingFriendRequests.isEmpty())
            .stream()
            .filter(incomingFriendRequest -> incomingFriendRequest.getSenderName().equals(username1))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Incoming friend request not found"))
            .accept();

        NotificationUtil.verifySuccessNotification(driver2, "Barátkérelem elfogadva.");

        AwaitilityWrapper.createDefault()
            .until(() -> !getFriends(driver1).isEmpty())
            .assertTrue("Friend did not appear.");

        AwaitilityWrapper.createDefault()
            .until(() -> !getFriends(driver2).isEmpty())
            .assertTrue("Friend did not appear.");
    }

    private static List<Friend> getFriends(WebDriver driver) {
        return MainMenuPage.friends(driver)
            .stream()
            .map(Friend::new)
            .collect(Collectors.toList());
    }

    private static List<WebElement> getFriendCandidates(WebDriver driver) {
        return MainMenuPage.friendCandidates(driver);
    }

    private static List<IncomingFriendRequest> getIncomingFriendRequests(WebDriver driver) {
        return MainMenuPage.incomingFriendRequests(driver)
            .stream()
            .map(IncomingFriendRequest::new)
            .collect(Collectors.toList());

    }
}
