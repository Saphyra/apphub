package com.github.saphyra.apphub.integration.action.frontend.community;

import com.github.saphyra.apphub.integration.action.frontend.common.CommonPageActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import org.openqa.selenium.WebDriver;

public class CommunityActions {
    public static void openFriendsTab(WebDriver driver) {
        CommunityPage.friendsButton(driver).click();
    }

    public static void openBlacklistTab(WebDriver driver) {
        CommunityPage.blacklistButton(driver).click();

        AwaitilityWrapper.createDefault()
            .until(() -> CommonPageActions.blacklistTab(driver).isDisplayed())
            .assertTrue("Blacklist tab is not displayed.");
    }

    public static void setUpFriendship(WebDriver senderDriver, String senderName, String query, WebDriver receiverDriver) {
        FriendRequestActions.sendFriendRequest(senderDriver, query);
        openFriendsTab(receiverDriver);

        AwaitilityWrapper.getListWithWait(() -> FriendRequestActions.getReceivedFriendRequests(receiverDriver), friendRequests -> !friendRequests.isEmpty())
            .stream()
            .filter(friendRequest -> friendRequest.getUsername().equals(senderName))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("FriendRequest not found."))
            .accept();
    }
}
