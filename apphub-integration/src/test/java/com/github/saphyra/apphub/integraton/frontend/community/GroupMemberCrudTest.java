package com.github.saphyra.apphub.integraton.frontend.community;

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
        List<WebDriver> drivers = extractDrivers(3);
        WebDriver driver1 = drivers.get(0);
        WebDriver driver2 = drivers.get(1);
        WebDriver driver3 = drivers.get(2);

        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        RegistrationParameters userData3 = RegistrationParameters.validParameters();

        RegistrationUtils.registerUsers(
            List.of(new BiWrapper<>(driver1, userData1), new BiWrapper<>(driver2, userData2), new BiWrapper<>(driver3, userData3)),
            (driver, registrationParameters) -> ModulesPageActions.openModule(driver, ModuleLocation.COMMUNITY)
        );

        CommunityActions.setUpFriendship(driver1, userData1.getUsername(), userData2.getUsername(), driver2);
        CommunityActions.setUpFriendship(driver1, userData1.getUsername(), userData3.getUsername(), driver3);

        GroupActions.createGroup(driver1, GROUP_NAME);

        //Check owner roles
        List<GroupMember> groupMembers = GroupActions.getMembers(driver1);
        assertThat(groupMembers).hasSize(1);
        GroupMember groupMember = groupMembers.get(0);
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

        //Search - User not found
        GroupActions.openAddGroupMemberWindow(driver1);

        GroupActions.fillAddGroupMemberInput(driver1, UUID.randomUUID().toString());
        SleepUtil.sleep(2000);
        GroupActions.verifyAddMemberUserNotFound(driver1);

        //Search - Query too short
        GroupActions.fillAddGroupMemberInput(driver1, "as");
        SleepUtil.sleep(2000);
        GroupActions.verifyAddMemberQueryTooShort(driver1);

        //Add member
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

        GroupActions.openGroup(driver2, GroupActions.getGroups(driver2).get(0));

        groupMembers = GroupActions.getMembers(driver2);
        assertThat(groupMembers).hasSize(2);
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

        //Modify roles
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

        groupMembers = GroupActions.getMembers(driver1);
        assertThat(groupMembers).hasSize(2);
        groupMember = groupMembers.get(1);
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

        //Invite member
        GroupActions.closeGroupDetails(driver2);
        GroupActions.openGroup(driver2, GroupActions.getGroups(driver2).get(0));

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

        //Kick member
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

        //Leave group
        GroupActions.openGroup(driver1, GroupActions.getGroups(driver1).get(0));

        GroupActions.leaveGroup(driver1);
        NotificationUtil.verifySuccessNotification(driver1, "Group left.");

        AwaitilityWrapper.createDefault()
            .until(() -> GroupActions.getGroups(driver1).isEmpty())
            .assertTrue("Group list is not empty.");

        GroupActions.closeGroupDetails(driver2);
        GroupActions.openGroup(driver2, GroupActions.getGroups(driver2).get(0));

        assertThat(GroupActions.getMembers(driver2)).hasSize(1);
    }
}
