package com.github.saphyra.apphub.integration.action.frontend.community;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

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
}
