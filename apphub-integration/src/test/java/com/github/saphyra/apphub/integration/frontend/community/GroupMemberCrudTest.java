package com.github.saphyra.apphub.integration.frontend.community;

import com.github.saphyra.apphub.integration.action.frontend.RegistrationUtils;
import com.github.saphyra.apphub.integration.action.frontend.common.CommonPageActions;
import com.github.saphyra.apphub.integration.action.frontend.community.CommunityActions;
import com.github.saphyra.apphub.integration.action.frontend.community.GroupActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.community.GroupMember;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupMemberCrudTest extends SeleniumTest {
    private static final String GROUP_NAME = "group-name";

    @Test(groups = {"fe", "community"})
    public void groupMemberCrud() {
        int serverPort = getServerPort();

        List<WebDriver> drivers = extractDrivers(3);
        WebDriver driver1 = drivers.get(0);
        WebDriver driver2 = drivers.get(1);
        WebDriver driver3 = drivers.get(2);

        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        RegistrationParameters userData3 = RegistrationParameters.validParameters();

        RegistrationUtils.registerUsers(
            serverPort,
            List.of(new BiWrapper<>(driver1, userData1), new BiWrapper<>(driver2, userData2), new BiWrapper<>(driver3, userData3)),
            (driver, registrationParameters) -> ModulesPageActions.openModule(serverPort, driver, ModuleLocation.COMMUNITY)
        );

        CommunityActions.setUpFriendship(driver1, userData1.getUsername(), userData2.getUsername(), driver2);
        CommunityActions.setUpFriendship(driver1, userData1.getUsername(), userData3.getUsername(), driver3);

        GroupActions.createGroup(driver1, GROUP_NAME);

        checkOwnerRoles(driver1, userData1);
        search_userNotFound(driver1);
        search_queryTooShort(driver1);
        addMember(driver1, driver2, userData2);
        modifyRoles(driver1, userData2);
        GroupMember groupMember = inviteMember(driver2, userData3);
        kickMember(driver1, driver2, groupMember);
        leaveGroup(driver1, driver2);
    }

    private static void checkOwnerRoles(WebDriver driver1, RegistrationParameters userData1) {
        List<GroupMember> groupMembers = AwaitilityWrapper.getListWithWait(() -> GroupActions.getMembers(driver1), m -> !m.isEmpty());
        assertThat(groupMembers).hasSize(1);
        GroupMember groupMember = groupMembers.getFirst();
        assertThat(groupMember.getUsername()).isEqualTo(userData1.getUsername());
        assertThat(groupMember.getEmail()).isEqualTo(userData1.getEmail());
        assertThat(groupMember.getCanInviteCheckbox().isEnabled()).isFalse();
        assertThat(groupMember.getCanInviteCheckbox().isSelected()).isTrue();
        assertThat(groupMember.getCanKickCheckbox().isEnabled()).isFalse();
        assertThat(groupMember.getCanKickCheckbox().isSelected()).isTrue();
        assertThat(groupMember.getCanModifyRolesCheckbox().isEnabled()).isFalse();
        assertThat(groupMember.getCanModifyRolesCheckbox().isSelected()).isTrue();
        assertThat(groupMember.getKickButton().isEnabled()).isFalse();
        assertThat(groupMember.getTransferLeadershipButton()).isNotEmpty();
        assertThat(groupMember.getTransferLeadershipButton().get().isEnabled()).isFalse();
    }

    private static void search_userNotFound(WebDriver driver1) {
        GroupActions.openAddGroupMemberWindow(driver1);

        GroupActions.fillAddGroupMemberInput(driver1, UUID.randomUUID().toString());
        GroupActions.verifyAddMemberUserNotFound(driver1);
    }

    private static void search_queryTooShort(WebDriver driver1) {
        GroupActions.fillAddGroupMemberInput(driver1, "as");
        GroupActions.verifyAddMemberQueryTooShort(driver1);
    }

    private static void addMember(WebDriver driver1, WebDriver driver2, RegistrationParameters userData2) {
        List<GroupMember> groupMembers;
        GroupMember groupMember;
        GroupActions.addMember(driver1, userData2.getUsername());

        NotificationUtil.verifySuccessNotification(driver1, "Group member added.");

        groupMembers = GroupActions.getMembers(driver1);
        assertThat(groupMembers).hasSize(2);
        groupMember = groupMembers.get(1);
        assertThat(groupMember.getUsername()).isEqualTo(userData2.getUsername());
        assertThat(groupMember.getEmail()).isEqualTo(userData2.getEmail());
        assertThat(groupMember.getCanInviteCheckbox().isEnabled()).isTrue();
        assertThat(groupMember.getCanInviteCheckbox().isSelected()).isFalse();
        assertThat(groupMember.getCanKickCheckbox().isEnabled()).isTrue();
        assertThat(groupMember.getCanKickCheckbox().isSelected()).isFalse();
        assertThat(groupMember.getCanModifyRolesCheckbox().isEnabled()).isTrue();
        assertThat(groupMember.getCanModifyRolesCheckbox().isSelected()).isFalse();
        assertThat(groupMember.getKickButton().isEnabled()).isTrue();
        assertThat(groupMember.getTransferLeadershipButton()).isNotEmpty();
        assertThat(groupMember.getTransferLeadershipButton().get().isEnabled()).isTrue();

        CommunityActions.openGroupsTab(driver2);

        WebElement group = AwaitilityWrapper.getSingleItemFromListWithWait(() -> GroupActions.getGroups(driver2));
        GroupActions.openGroup(driver2, group);

        groupMembers = AwaitilityWrapper.getListWithWait(() -> GroupActions.getMembers(driver2), m -> m.size() == 2);
        groupMember = groupMembers.get(1);
        assertThat(groupMember.getUsername()).isEqualTo(userData2.getUsername());
        assertThat(groupMember.getEmail()).isEqualTo(userData2.getEmail());
        assertThat(groupMember.getCanInviteCheckbox().isEnabled()).isFalse();
        assertThat(groupMember.getCanInviteCheckbox().isSelected()).isFalse();
        assertThat(groupMember.getCanKickCheckbox().isEnabled()).isFalse();
        assertThat(groupMember.getCanKickCheckbox().isSelected()).isFalse();
        assertThat(groupMember.getCanModifyRolesCheckbox().isEnabled()).isFalse();
        assertThat(groupMember.getCanModifyRolesCheckbox().isSelected()).isFalse();
        assertThat(groupMember.getKickButton().isEnabled()).isFalse();
        assertThat(groupMember.getTransferLeadershipButton()).isEmpty();
    }

    private static void modifyRoles(WebDriver driver1, RegistrationParameters userData2) {
        GroupActions.getMembers(driver1)
            .get(1)
            .getCanInviteCheckbox()
            .click();
        CommonPageActions.confirmConfirmationDialog(driver1, "grant-role-to-member-confirmation-dialog");
        SleepUtil.sleep(1000);

        GroupActions.getMembers(driver1)
            .get(1)
            .getCanKickCheckbox()
            .click();
        CommonPageActions.confirmConfirmationDialog(driver1, "grant-role-to-member-confirmation-dialog");
        SleepUtil.sleep(1000);

        GroupActions.getMembers(driver1)
            .get(1)
            .getCanModifyRolesCheckbox()
            .click();
        CommonPageActions.confirmConfirmationDialog(driver1, "grant-role-to-member-confirmation-dialog");
        SleepUtil.sleep(1000);

        AwaitilityWrapper.awaitAssert(() -> {
            List<GroupMember> groupMembers = GroupActions.getMembers(driver1);
            assertThat(groupMembers).hasSize(2);
            GroupMember groupMember = groupMembers.get(1);

            assertThat(groupMember.getUsername()).isEqualTo(userData2.getUsername());
            assertThat(groupMember.getEmail()).isEqualTo(userData2.getEmail());
            assertThat(groupMember.getCanInviteCheckbox().isEnabled()).isTrue();
            assertThat(groupMember.getCanInviteCheckbox().isSelected()).isTrue();
            assertThat(groupMember.getCanKickCheckbox().isEnabled()).isTrue();
            assertThat(groupMember.getCanKickCheckbox().isSelected()).isTrue();
            assertThat(groupMember.getCanModifyRolesCheckbox().isEnabled()).isTrue();
            assertThat(groupMember.getCanModifyRolesCheckbox().isSelected()).isTrue();
            assertThat(groupMember.getKickButton().isEnabled()).isTrue();
            assertThat(groupMember.getTransferLeadershipButton()).isNotEmpty();
            assertThat(groupMember.getTransferLeadershipButton().get().isEnabled()).isTrue();
        });
    }

    private static GroupMember inviteMember(WebDriver driver2, RegistrationParameters userData3) {
        List<GroupMember> groupMembers;
        GroupMember groupMember;
        GroupActions.closeGroupDetails(driver2);
        GroupActions.openGroup(driver2, GroupActions.getGroups(driver2).getFirst());

        GroupActions.addMember(driver2, userData3.getUsername());

        NotificationUtil.verifySuccessNotification(driver2, "Group member added.");

        groupMembers = GroupActions.getMembers(driver2);
        assertThat(groupMembers).hasSize(3);
        groupMember = groupMembers.stream()
            .filter(gm -> gm.getUsername().equals(userData3.getUsername()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("GroupMember not found."));

        assertThat(groupMember.getUsername()).isEqualTo(userData3.getUsername());
        assertThat(groupMember.getEmail()).isEqualTo(userData3.getEmail());
        assertThat(groupMember.getCanInviteCheckbox().isEnabled()).isTrue();
        assertThat(groupMember.getCanInviteCheckbox().isSelected()).isFalse();
        assertThat(groupMember.getCanKickCheckbox().isEnabled()).isTrue();
        assertThat(groupMember.getCanKickCheckbox().isSelected()).isFalse();
        assertThat(groupMember.getCanModifyRolesCheckbox().isEnabled()).isTrue();
        assertThat(groupMember.getCanModifyRolesCheckbox().isSelected()).isFalse();
        assertThat(groupMember.getKickButton().isEnabled()).isTrue();
        assertThat(groupMember.getTransferLeadershipButton()).isEmpty();
        return groupMember;
    }

    private static void kickMember(WebDriver driver1, WebDriver driver2, GroupMember groupMember) {
        List<GroupMember> groupMembers;
        groupMember.getKickButton()
            .click();
        CommonPageActions.confirmConfirmationDialog(driver2, "kick-group-member-confirmation-dialog");
        NotificationUtil.verifySuccessNotification(driver2, "Group member kicked.");

        groupMembers = GroupActions.getMembers(driver2);
        assertThat(groupMembers).hasSize(2);

        //Transfer ownership
        GroupActions.getMembers(driver1)
            .get(1)
            .getTransferLeadershipButton()
            .filter(WebElement::isEnabled)
            .orElseThrow(() -> new NullPointerException("Transfer ownership button not present or not enabled."))
            .click();
        CommonPageActions.confirmConfirmationDialog(driver1, "transfer-ownership-confirmation-dialog");
        NotificationUtil.verifySuccessNotification(driver1, "Ownership transferred.");
    }

    private static void leaveGroup(WebDriver driver1, WebDriver driver2) {
        GroupActions.openGroup(driver1, GroupActions.getGroups(driver1).getFirst());

        GroupActions.leaveGroup(driver1);
        NotificationUtil.verifySuccessNotification(driver1, "Group left.");

        AwaitilityWrapper.createDefault()
            .until(() -> GroupActions.getGroups(driver1).isEmpty())
            .assertTrue("Group list is not empty.");

        GroupActions.closeGroupDetails(driver2);
        GroupActions.openGroup(driver2, GroupActions.getGroups(driver2).getFirst());

        assertThat(GroupActions.getMembers(driver2)).hasSize(1);
    }
}
