package com.github.saphyra.apphub.integration.action.frontend.community;

import com.github.saphyra.apphub.integration.action.frontend.common.CommonPageActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.api.community.GroupInvitationType;
import com.github.saphyra.apphub.integration.structure.api.community.GroupMember;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;
import static org.assertj.core.api.Assertions.assertThat;

public class GroupActions {
    public static void addMember(WebDriver driver, String query) {
        if (!CommunityPage.addGroupMemberWindow(driver).isDisplayed()) {
            CommunityPage.addGroupMemberButton(driver).click();
        }

        fillAddGroupMemberInput(driver, query);

        AwaitilityWrapper.awaitAssert(() -> assertThat(CommunityPage.groupAddMemberSearchResult(driver).isDisplayed()).isTrue());

        CommunityPage.groupAddMemberSearchResultItems(driver)
            .getFirst()
            .click();
    }

    public static void createGroup(WebDriver driver, String groupName) {
        CommunityActions.openGroupsTab(driver);

        openCreateGroupWindow(driver);

        fillCreateGroupNameInput(driver, groupName);

        submitCreateGroupForm(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> CommunityPage.groupDetailsWindow(driver).isDisplayed())
            .assertTrue("GroupDetails window is not displayed.");
    }

    public static void openCreateGroupWindow(WebDriver driver) {
        CommunityPage.openCreateGroupWindowButton(driver)
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> CommunityPage.createGroupWindow(driver).isDisplayed())
            .assertTrue("CreateGroup window is not displayed");
    }

    public static void fillCreateGroupNameInput(WebDriver driver, String groupName) {
        clearAndFill(CommunityPage.createGroupNameInput(driver), groupName);
    }

    public static void verifyCreateGroupNameError(WebDriver driver, String errorMessage) {
        WebElementUtils.verifyInvalidFieldState(CommunityPage.createGroupInvalidName(driver), true, errorMessage);
    }

    public static void verifyCreateGroupNameCorrect(WebDriver driver) {
        WebElementUtils.verifyInvalidFieldStateLegacy(CommunityPage.createGroupInvalidName(driver), false, null);
    }

    public static void submitCreateGroupForm(WebDriver driver) {
        WebElement button = CommunityPage.createGroupButton(driver);

        AwaitilityWrapper.createDefault()
            .until(button::isEnabled)
            .assertTrue("Create Group button is not enabled");

        button.click();
    }

    public static void closeGroupDetails(WebDriver driver) {
        CommunityPage.closeGroupDetailsButton(driver)
            .click();
    }

    public static List<WebElement> getGroups(WebDriver driver) {
        return CommunityPage.groups(driver);
    }

    public static void openGroup(WebDriver driver, WebElement webElement) {
        webElement.click();

        AwaitilityWrapper.createDefault()
            .until(() -> CommunityPage.groupDetailsWindow(driver).isDisplayed())
            .assertTrue("GroupDetails window is not opened.");
    }

    public static void setGroupName(WebDriver driver, String groupName) {
        WebElementUtils.clearAndFillContentEditable(driver, CommunityPage.groupDetailsName(driver), groupName);

        CommunityPage.groupDetailsInvitationTypeWrapper(driver)
            .click();
    }

    public static String getGroupName(WebDriver driver) {
        return CommunityPage.groupDetailsName(driver)
            .getText();
    }

    public static void changeInvitationType(WebDriver driver, GroupInvitationType invitationType) {
        WebElementUtils.selectOptionById(CommunityPage.invitationTypeSelectMenu(driver), invitationType.getId());
    }

    public static void disbandGroup(WebDriver driver) {
        CommunityPage.disbandGroupButton(driver)
            .click();

        CommonPageActions.confirmConfirmationDialog(driver, "disband-group-confirmation-dialog");
    }

    public static void openAddGroupMemberWindow(WebDriver driver) {
        WebElement button = CommunityPage.addGroupMemberButton(driver);

        assertThat(button.isEnabled()).isTrue();

        button.click();

        AwaitilityWrapper.createDefault()
            .until(() -> CommunityPage.addGroupMemberWindow(driver).isDisplayed())
            .assertTrue("Add GroupMember page is not displayed");
    }

    public static void fillAddGroupMemberInput(WebDriver driver, String query) {
        clearAndFill(CommunityPage.addGroupMemberSearchInput(driver), query);
    }

    public static void verifyAddMemberUserNotFound(WebDriver driver) {
        AwaitilityWrapper.awaitAssert(() -> assertThat(CommunityPage.groupAddMemberResultNoResult(driver).isDisplayed()).isTrue());
    }

    public static void verifyAddMemberQueryTooShort(WebDriver driver) {
        AwaitilityWrapper.awaitAssert(() -> assertThat(CommunityPage.groupAddMemberResultQueryTooShort(driver).isDisplayed()).isTrue());
    }

    public static List<GroupMember> getMembers(WebDriver driver) {
        return CommunityPage.groupMembers(driver)
            .stream()
            .map(GroupMember::new)
            .collect(Collectors.toList());
    }

    public static void leaveGroup(WebDriver driver) {
        CommunityPage.leaveGroupButton(driver)
            .click();

        CommonPageActions.confirmConfirmationDialog(driver, "leave-group-confirmation-dialog");
    }
}
