package com.github.saphyra.apphub.integration.backend.community.group;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.community.CommunityActions;
import com.github.saphyra.apphub.integration.action.backend.community.GroupActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DynamoDbUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.community.GroupInvitationType;
import com.github.saphyra.apphub.integration.structure.api.community.GroupListResponse;
import com.github.saphyra.apphub.integration.structure.api.community.GroupMemberResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupCrudTest extends BackEndTest {
    private static final String GROUP_NAME = "group-name";
    private static final String NEW_GROUP_NAME = "new-group-name";

    @Test(groups = {"be", "community"})
    public void groupCrud() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        String accessToken1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        UUID userId1 = DynamoDbUtil.getUserIdByEmail(userData1.getEmail());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        String accessToken2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);
        UUID userId2 = DynamoDbUtil.getUserIdByEmail(userData2.getEmail());

        CommunityActions.setUpFriendship(getServerPort(), accessToken1, accessToken2, userId2);

        create_nameNull(accessToken1);
        rename_nameTooShort(accessToken1);
        create_nameTooLong(accessToken1);
        GroupListResponse group = create(accessToken1, userId1);
        rename_nameNull(accessToken1, group);
        rename_nameTooShort(accessToken1, group);
        rename_nameTooLong(accessToken1, group);
        GroupMemberResponse groupMember = rename_notOwner(accessToken1, accessToken2, userId2, group);
        group = rename(accessToken1, group);
        changeInvitationType_null(accessToken1, group);
        changeInvitationType_notOwner(accessToken2, group);
        group = changeInvitationType(accessToken1, group);
        changeOwner_null(accessToken1, group);
        changeOwner_notOwner(accessToken2, group, groupMember);
        changeOwner_alreadyOwner(accessToken1, userId1, group);
        changeOwner(accessToken1, group, groupMember);
        deleteGroup_notOwner(accessToken1, group);
        deleteGroup(accessToken1, accessToken2, group);
    }

    private static void create_nameNull(String accessToken1) {
        ResponseValidator.verifyInvalidParam(
            GroupActions.getCreateGroupResponse(getServerPort(), accessToken1, null),
            "groupName",
            "must not be null"
        );
    }

    private static void rename_nameTooShort(String accessToken1) {
        ResponseValidator.verifyInvalidParam(
            GroupActions.getCreateGroupResponse(getServerPort(), accessToken1, "as"),
            "groupName",
            "too short"
        );
    }

    private static void create_nameTooLong(String accessToken1) {
        ResponseValidator.verifyInvalidParam(
            GroupActions.getCreateGroupResponse(getServerPort(), accessToken1, Stream.generate(() -> "a").limit(31).collect(Collectors.joining())),
            "groupName",
            "too long"
        );
    }

    private static GroupListResponse create(String accessToken1, UUID userId1) {
        GroupListResponse group = GroupActions.createGroup(getServerPort(), accessToken1, GROUP_NAME);

        assertThat(group.getName()).isEqualTo(GROUP_NAME);
        assertThat(group.getOwnerId()).isEqualTo(userId1);
        assertThat(group.getInvitationType()).isEqualTo(GroupInvitationType.FRIENDS);
        return group;
    }

    private static void rename_nameNull(String accessToken1, GroupListResponse group) {
        ResponseValidator.verifyInvalidParam(
            GroupActions.getRenameGroupResponse(getServerPort(), accessToken1, group.getGroupId(), null),
            "groupName",
            "must not be null"
        );
    }

    private static void rename_nameTooShort(String accessToken1, GroupListResponse group) {
        ResponseValidator.verifyInvalidParam(
            GroupActions.getRenameGroupResponse(getServerPort(), accessToken1, group.getGroupId(), "as"),
            "groupName",
            "too short"
        );
    }

    private static void rename_nameTooLong(String accessToken1, GroupListResponse group) {
        ResponseValidator.verifyInvalidParam(
            GroupActions.getRenameGroupResponse(getServerPort(), accessToken1, group.getGroupId(), Stream.generate(() -> "a").limit(31).collect(Collectors.joining())),
            "groupName",
            "too long"
        );
    }

    private static GroupMemberResponse rename_notOwner(String accessToken1, String accessToken2, UUID userId2, GroupListResponse group) {
        GroupMemberResponse groupMember = GroupActions.createMember(getServerPort(), accessToken1, group.getGroupId(), userId2);
        ResponseValidator.verifyForbiddenOperation(GroupActions.getRenameGroupResponse(getServerPort(), accessToken2, group.getGroupId(), NEW_GROUP_NAME));
        return groupMember;
    }

    private static GroupListResponse rename(String accessToken1, GroupListResponse group) {
        group = GroupActions.renameGroup(getServerPort(), accessToken1, group.getGroupId(), NEW_GROUP_NAME);

        assertThat(group.getName()).isEqualTo(NEW_GROUP_NAME);
        return group;
    }

    private static void changeInvitationType_null(String accessToken1, GroupListResponse group) {
        ResponseValidator.verifyInvalidParam(
            GroupActions.getChangeInvitationTypeResponse(getServerPort(), accessToken1, group.getGroupId(), null),
            "invitationType",
            "must not be null"
        );
    }

    private static void changeInvitationType_notOwner(String accessToken2, GroupListResponse group) {
        ResponseValidator.verifyForbiddenOperation(GroupActions.getChangeInvitationTypeResponse(getServerPort(), accessToken2, group.getGroupId(), GroupInvitationType.FRIENDS_OF_FRIENDS));
    }

    private static GroupListResponse changeInvitationType(String accessToken1, GroupListResponse group) {
        group = GroupActions.changeInvitationType(getServerPort(), accessToken1, group.getGroupId(), GroupInvitationType.FRIENDS_OF_FRIENDS);

        assertThat(group.getInvitationType()).isEqualTo(GroupInvitationType.FRIENDS_OF_FRIENDS);
        return group;
    }

    private static void changeOwner_null(String accessToken1, GroupListResponse group) {
        ResponseValidator.verifyInvalidParam(
            GroupActions.getChangeOwnerResponse(getServerPort(), accessToken1, group.getGroupId(), null),
            "groupMemberId",
            "must not be null"
        );
    }

    private static void changeOwner_notOwner(String accessToken2, GroupListResponse group, GroupMemberResponse groupMember) {
        ResponseValidator.verifyForbiddenOperation(GroupActions.getChangeOwnerResponse(getServerPort(), accessToken2, group.getGroupId(), groupMember.getGroupMemberId()));
    }

    private void changeOwner_alreadyOwner(String accessToken1, UUID userId1, GroupListResponse group) {
        ResponseValidator.verifyErrorResponse(
            GroupActions.getChangeOwnerResponse(getServerPort(), accessToken1, group.getGroupId(), getOwnMember(accessToken1, group.getGroupId(), userId1).getGroupMemberId()),
            409,
            ErrorCode.GENERAL_ERROR
        );
    }

    private static void changeOwner(String accessToken1, GroupListResponse group, GroupMemberResponse groupMember) {
        GroupActions.changeOwner(getServerPort(), accessToken1, group.getGroupId(), groupMember.getGroupMemberId());

        assertThat(GroupActions.getGroups(getServerPort(), accessToken1).getFirst().getOwnerId()).isEqualTo(groupMember.getUserId());
    }

    private static void deleteGroup_notOwner(String accessToken1, GroupListResponse group) {
        ResponseValidator.verifyForbiddenOperation(

            GroupActions.getDeleteGroupResponse(getServerPort(), accessToken1, group.getGroupId())
        );
    }

    private static void deleteGroup(String accessToken1, String accessToken2, GroupListResponse group) {
        GroupActions.deleteGroup(getServerPort(), accessToken2, group.getGroupId());

        assertThat(GroupActions.getGroups(getServerPort(), accessToken1)).isEmpty();
        assertThat(GroupActions.getGroups(getServerPort(), accessToken2)).isEmpty();
    }

    private GroupMemberResponse getOwnMember(String accessToken, UUID groupId, UUID userId) {
        return GroupActions.getMembers(getServerPort(), accessToken, groupId)
            .stream()
            .filter(groupMemberResponse -> groupMemberResponse.getUserId().equals(userId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("GroupMember not found for userId " + userId + " in group " + groupId));
    }
}
