package com.github.saphyra.apphub.integration.action.frontend.community;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.api.community.FriendRequest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class FriendRequestActions {
    public static void sendFriendRequest(WebDriver driver, String query) {
        fillSearchForm(driver, query);

        AwaitilityWrapper.createDefault()
            .until(() -> CommunityPage.createFriendRequestSearchResult(driver).isDisplayed())
            .assertTrue("Search result not displayed");

        List<WebElement> result = AwaitilityWrapper.getListWithWait(() -> CommunityPage.createFriendRequestSearchResultItems(driver), searchResult -> !searchResult.isEmpty());

        result.get(0)
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> !CommunityPage.createFriendRequestPage(driver).isDisplayed())
            .assertTrue("Create FriendRequest page is not closed");

        NotificationUtil.verifySuccessNotification(driver, "Friend request sent.");
    }

    public static void fillSearchForm(WebDriver driver, String query) {
        if (!CommunityPage.createFriendRequestPage(driver).isDisplayed()) {
            CommunityPage.addFriendButton(driver).click();
        }

        AwaitilityWrapper.createDefault()
            .until(() -> CommunityPage.createFriendRequestPage(driver).isDisplayed())
            .assertTrue("Create FriendRequest page is not displayed.");

        WebElementUtils.clearAndFill(CommunityPage.createFriendRequestSearchInput(driver), query);
    }

    public static void verifyUserNotFound(WebDriver driver) {
        AwaitilityWrapper.createDefault()
            .until(() -> CommunityPage.createFriendRequestSearchResultNoResult(driver).isDisplayed())
            .assertTrue("User not found message is not displayed.");
    }

    public static void verifyQueryTooShort(WebDriver driver) {
        AwaitilityWrapper.createDefault()
            .until(() -> CommunityPage.createFriendRequestSearchResultQueryTooShort(driver).isDisplayed())
            .assertTrue("Query too short message is not displayed.");
    }

    public static List<FriendRequest> getSentFriendRequests(WebDriver driver) {
        return CommunityPage.sentFriendRequests(driver)
            .stream()
            .map(FriendRequest::new)
            .collect(Collectors.toList());
    }

    public static List<FriendRequest> getReceivedFriendRequests(WebDriver driver) {
        return CommunityPage.receivedFriendRequests(driver)
            .stream()
            .map(FriendRequest::new)
            .collect(Collectors.toList());
    }
}
