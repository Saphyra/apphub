package com.github.saphyra.apphub.integration.action.frontend.community;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.api.community.Blacklist;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class BlacklistActions {
    public static void createBlacklist(WebDriver driver, String query) {
        fillSearchForm(driver, query);

        AwaitilityWrapper.createDefault()
            .until(() -> CommunityPage.createBlacklistSearchResult(driver).isDisplayed())
            .assertTrue("Search result not displayed");

        List<WebElement> result = AwaitilityWrapper.getListWithWait(() -> CommunityPage.createBlacklistSearchResultItems(driver), searchResult -> !searchResult.isEmpty());

        result.get(0)
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> !CommunityPage.createBlacklistPage(driver).isDisplayed())
            .assertTrue("Create Blacklist page is not closed");

        NotificationUtil.verifySuccessNotification(driver, "User blocked.");
    }

    public static void fillSearchForm(WebDriver driver, String query) {
        if (!CommunityPage.createBlacklistPage(driver).isDisplayed()) {
            CommunityPage.addBlacklistButton(driver).click();
        }

        AwaitilityWrapper.createDefault()
            .until(() -> CommunityPage.createBlacklistPage(driver).isDisplayed())
            .assertTrue("Create FriendRequest page is not displayed.");

        WebElementUtils.clearAndFill(CommunityPage.createBlacklistSearchInput(driver), query);
    }

    public static void verifyUserNotFound(WebDriver driver) {
        AwaitilityWrapper.createDefault()
            .until(() -> CommunityPage.createBlacklistSearchResultNoResult(driver).isDisplayed())
            .assertTrue("User not found message is not displayed.");
    }

    public static void verifyQueryTooShort(WebDriver driver) {
        AwaitilityWrapper.createDefault()
            .until(() -> CommunityPage.createBlacklistSearchResultQueryTooShort(driver).isDisplayed())
            .assertTrue("Query too short message is not displayed.");
    }

    public static List<Blacklist> getBlacklist(WebDriver driver) {
        return CommunityPage.blacklists(driver)
            .stream()
            .map(Blacklist::new)
            .collect(Collectors.toList());
    }
}
