package com.github.saphyra.apphub.integration.action.frontend.community;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Optional;

class CommunityPage {
    public static WebElement addFriendButton(WebDriver driver) {
        return driver.findElement(By.id("contacts-add-friend-button"));
    }

    public static WebElement createFriendRequestPage(WebDriver driver) {
        return driver.findElement(By.id("create-friend-request"));
    }

    public static WebElement createFriendRequestSearchInput(WebDriver driver) {
        return driver.findElement(By.id("create-friend-request-search-input"));
    }

    public static List<WebElement> createFriendRequestSearchResultItems(WebDriver driver) {
        return driver.findElements(By.cssSelector("#create-friend-request-search-result div.button"));
    }

    public static WebElement createFriendRequestSearchResultNoResult(WebDriver driver) {
        return driver.findElement(By.id("create-friend-request-search-result-no-result"));
    }

    public static WebElement createFriendRequestSearchResultQueryTooShort(WebDriver driver) {
        return driver.findElement(By.id("create-friend-request-search-result-query-too-short"));
    }

    public static List<WebElement> sentFriendRequests(WebDriver driver) {
        return driver.findElements(By.cssSelector("#sent-friend-requests-content .list-item"));
    }

    public static WebElement friendsButton(WebDriver driver) {
        return driver.findElement(By.id("friends-button"));
    }

    public static List<WebElement> receivedFriendRequests(WebDriver driver) {
        return driver.findElements(By.cssSelector("#received-friend-requests-content .list-item"));
    }

    public static WebElement createFriendRequestSearchResult(WebDriver driver) {
        return driver.findElement(By.id("create-friend-request-search-result"));
    }

    public static List<WebElement> friendships(WebDriver driver) {
        return driver.findElements(By.cssSelector("#friends-list-content .list-item"));
    }

    public static WebElement blacklistButton(WebDriver driver) {
        return driver.findElement(By.id("blacklist-button"));
    }

    public static WebElement createBlacklistPage(WebDriver driver) {
        return driver.findElement(By.id("create-blacklist"));
    }

    public static WebElement addBlacklistButton(WebDriver driver) {
        return driver.findElement(By.id("contacts-blacklist-add-button"));
    }

    public static WebElement createBlacklistSearchInput(WebDriver driver) {
        return driver.findElement(By.id("create-blacklist-search-input"));
    }

    public static WebElement createBlacklistSearchResultNoResult(WebDriver driver) {
        return driver.findElement(By.id("create-blacklist-search-result-no-result"));
    }

    public static WebElement createBlacklistSearchResultQueryTooShort(WebDriver driver) {
        return driver.findElement(By.id("create-blacklist-search-result-query-too-short"));
    }

    public static WebElement createBlacklistSearchResult(WebDriver driver) {
        return driver.findElement(By.id("create-blacklist-search-result"));
    }

    public static List<WebElement> createBlacklistSearchResultItems(WebDriver driver) {
        return driver.findElements(By.cssSelector("#create-blacklist-search-result div.button"));
    }

    public static List<WebElement> blacklists(WebDriver driver) {
        return driver.findElements(By.cssSelector("#contacts-blacklist-list .list-item"));
    }

    public static WebElement groupsButton(WebDriver driver) {
        return driver.findElement(By.id("groups-button"));
    }

    public static WebElement blacklistTab(WebDriver driver) {
        return driver.findElement(By.id("contacts-blacklist"));
    }

    public static WebElement groupsTab(WebDriver driver) {
        return driver.findElement(By.id("contacts-groups"));
    }

    public static WebElement openCreateGroupWindowButton(WebDriver driver) {
        return driver.findElement(By.id("contacts-groups-create-button"));
    }

    public static WebElement createGroupWindow(WebDriver driver) {
        return driver.findElement(By.id("create-group"));
    }

    public static WebElement createGroupNameInput(WebDriver driver) {
        return driver.findElement(By.id("create-group-name-input"));
    }

    public static Optional<WebElement> createGroupInvalidName(WebDriver driver) {
        return driver.findElements(By.id("create-group-invalid-name"))
            .stream()
            .findFirst();
    }

    public static WebElement createGroupButton(WebDriver driver) {
        return driver.findElement(By.id("create-group-button"));
    }

    public static WebElement closeGroupDetailsButton(WebDriver driver) {
        return driver.findElement(By.cssSelector("#group-details header .close-dialog-button"));
    }

    public static List<WebElement> groups(WebDriver driver) {
        return driver.findElements(By.cssSelector("#contacts-groups-list .list-item"));
    }

    public static WebElement groupDetailsWindow(WebDriver driver) {
        return driver.findElement(By.id("group-details"));
    }

    public static WebElement groupDetailsName(WebDriver driver) {
        return driver.findElement(By.id("group-details-title"));
    }

    public static WebElement groupDetailsInvitationTypeWrapper(WebDriver driver) {
        return driver.findElement(By.id("group-details-invitation-type-wrapper"));
    }

    public static WebElement invitationTypeSelectMenu(WebDriver driver) {
        return driver.findElement(By.id("group-details-invitation-type"));
    }

    public static WebElement disbandGroupButton(WebDriver driver) {
        return driver.findElement(By.id("disband-group-button"));
    }

    public static WebElement addGroupMemberButton(WebDriver driver) {
        return driver.findElement(By.id("group-details-add-member-button"));
    }

    public static WebElement addGroupMemberWindow(WebDriver driver) {
        return driver.findElement(By.id("group-add-member"));
    }

    public static WebElement addGroupMemberSearchInput(WebDriver driver) {
        return driver.findElement(By.id("group-add-member-search-input"));
    }

    public static WebElement groupAddMemberResultNoResult(WebDriver driver) {
        return driver.findElement(By.id("group-add-member-search-result-no-result"));
    }

    public static WebElement groupAddMemberResultQueryTooShort(WebDriver driver) {
        return driver.findElement(By.id("group-add-member-search-result-query-too-short"));
    }

    public static WebElement groupAddMemberSearchResult(WebDriver driver) {
        return driver.findElement(By.id("group-add-member-search-result"));
    }

    public static List<WebElement> groupAddMemberSearchResultItems(WebDriver driver) {
        return driver.findElements(By.cssSelector("#group-add-member-search-result div.button"));
    }

    public static List<WebElement> groupMembers(WebDriver driver) {
        return driver.findElements(By.cssSelector("#group-details-members tr"));
    }

    public static WebElement leaveGroupButton(WebDriver driver) {
        return driver.findElement(By.id("leave-group-button"));
    }
}
