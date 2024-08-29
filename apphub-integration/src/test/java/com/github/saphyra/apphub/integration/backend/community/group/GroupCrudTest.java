package com.github.saphyra.apphub.integration.backend.community.group;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.community.CommunityActions;
import com.github.saphyra.apphub.integration.action.backend.community.GroupActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
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
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());

        CommunityActions.setUpFriendship(getServerPort(), accessTokenId1, accessTokenId2, userId2);

        create_nameNull(accessTokenId1);
        rename_nameTooShort(accessTokenId1);
        create_nameTooLong(accessTokenId1);
        GroupListResponse group = create(accessTokenId1, userId1);
        rename_nameNull(accessTokenId1, group);
        rename_nameTooShort(accessTokenId1, group);
        rename_nameTooLong(accessTokenId1, group);
        GroupMemberResponse groupMember = rename_notOwner(accessTokenId1, accessTokenId2, userId2, group);
        group = rename(accessTokenId1, group);
        changeInvitationType_null(accessTokenId1, group);
        changeInvitationType_notOwner(accessTokenId2, group);
        group = changeInvitationType(accessTokenId1, group);
        changeOwner_null(accessTokenId1, group);
        changeOwner_notOwner(accessTokenId2, group, groupMember);
        changeOwner_alreadyOwner(accessTokenId1, userId1, group);
        changeOwner(accessTokenId1, group, groupMember);
        deleteGroup_notOwner(accessTokenId1, group);
        deleteGroup(accessTokenId1, accessTokenId2, group);
    }

    private static void create_nameNull(UUID accessTokenId1) {
        ResponseValidator.verifyInvalidParam(
            GroupActions.getCreateGroupResponse(getServerPort(), accessTokenId1, null),
            "groupName",
            "must not be null"
        );
    }

    private static void rename_nameTooShort(UUID accessTokenId1) {
        ResponseValidator.verifyInvalidParam(
            GroupActions.getCreateGroupResponse(getServerPort(), accessTokenId1, "as"),
            "groupName",
            "too short"
        );
    }

    private static void create_nameTooLong(UUID accessTokenId1) {
        ResponseValidator.verifyInvalidParam(
            GroupActions.getCreateGroupResponse(getServerPort(), accessTokenId1, Stream.generate(() -> "a").limit(31).collect(Collectors.joining())),
            "groupName",
            "too long"
        );
    }

    private static GroupListResponse create(UUID accessTokenId1, UUID userId1) {
        GroupListResponse group = GroupActions.createGroup(getServerPort(), accessTokenId1, GROUP_NAME);

        assertThat(group.getName()).isEqualTo(GROUP_NAME);
        assertThat(group.getOwnerId()).isEqualTo(userId1);
        assertThat(group.getInvitationType()).isEqualTo(GroupInvitationType.FRIENDS);
        return group;
    }

    private static void rename_nameNull(UUID accessTokenId1, GroupListResponse group) {
        ResponseValidator.verifyInvalidParam(
            GroupActions.getRenameGroupResponse(getServerPort(), accessTokenId1, group.getGroupId(), null),
            "groupName",
            "must not be null"
        );
    }

    private static void rename_nameTooShort(UUID accessTokenId1, GroupListResponse group) {
        ResponseValidator.verifyInvalidParam(
            GroupActions.getRenameGroupResponse(getServerPort(), accessTokenId1, group.getGroupId(), "as"),
            "groupName",
            "too short"
        );
    }

    private static void rename_nameTooLong(UUID accessTokenId1, GroupListResponse group) {
        ResponseValidator.verifyInvalidParam(
            GroupActions.getRenameGroupResponse(getServerPort(), accessTokenId1, group.getGroupId(), Stream.generate(() -> "a").limit(31).collect(Collectors.joining())),
            "groupName",
            "too long"
        );
    }

    private static GroupMemberResponse rename_notOwner(UUID accessTokenId1, UUID accessTokenId2, UUID userId2, GroupListResponse group) {
        GroupMemberResponse groupMember = GroupActions.createMember(getServerPort(), accessTokenId1, group.getGroupId(), userId2);
        ResponseValidator.verifyForbiddenOperation(GroupActions.getRenameGroupResponse(getServerPort(), accessTokenId2, group.getGroupId(), NEW_GROUP_NAME));
        return groupMember;
    }

    private static GroupListResponse rename(UUID accessTokenId1, GroupListResponse group) {
        group = GroupActions.renameGroup(getServerPort(), accessTokenId1, group.getGroupId(), NEW_GROUP_NAME);

        assertThat(group.getName()).isEqualTo(NEW_GROUP_NAME);
        return group;
    }

    private static void changeInvitationType_null(UUID accessTokenId1, GroupListResponse group) {
        ResponseValidator.verifyInvalidParam(
            GroupActions.getChangeInvitationTypeResponse(getServerPort(), accessTokenId1, group.getGroupId(), null),
            "invitationType",
            "must not be null"
        );
    }

    private static void changeInvitationType_notOwner(UUID accessTokenId2, GroupListResponse group) {
        ResponseValidator.verifyForbiddenOperation(GroupActions.getChangeInvitationTypeResponse(getServerPort(), accessTokenId2, group.getGroupId(), GroupInvitationType.FRIENDS_OF_FRIENDS));
    }

    private static GroupListResponse changeInvitationType(UUID accessTokenId1, GroupListResponse group) {
        group = GroupActions.changeInvitationType(getServerPort(), accessTokenId1, group.getGroupId(), GroupInvitationType.FRIENDS_OF_FRIENDS);

        assertThat(group.getInvitationType()).isEqualTo(GroupInvitationType.FRIENDS_OF_FRIENDS);
        return group;
    }

    private static void changeOwner_null(UUID accessTokenId1, GroupListResponse group) {
        ResponseValidator.verifyInvalidParam(
            GroupActions.getChangeOwnerResponse(getServerPort(), accessTokenId1, group.getGroupId(), null),
            "groupMemberId",
            "must not be null"
        );
    }

    private static void changeOwner_notOwner(UUID accessTokenId2, GroupListResponse group, GroupMemberResponse groupMember) {
        ResponseValidator.verifyForbiddenOperation(GroupActions.getChangeOwnerResponse(getServerPort(), accessTokenId2, group.getGroupId(), groupMember.getGroupMemberId()));
    }

    private void changeOwner_alreadyOwner(UUID accessTokenId1, UUID userId1, GroupListResponse group) {
        ResponseValidator.verifyErrorResponse(
            GroupActions.getChangeOwnerResponse(getServerPort(), accessTokenId1, group.getGroupId(), getOwnMember(accessTokenId1, group.getGroupId(), userId1).getGroupMemberId()),
            409,
            ErrorCode.GENERAL_ERROR
        );
    }

    private static void changeOwner(UUID accessTokenId1, GroupListResponse group, GroupMemberResponse groupMember) {
        GroupActions.changeOwner(getServerPort(), accessTokenId1, group.getGroupId(), groupMember.getGroupMemberId());

        assertThat(GroupActions.getGroups(getServerPort(), accessTokenId1).get(0).getOwnerId()).isEqualTo(groupMember.getUserId());
    }

    private static void deleteGroup_notOwner(UUID accessTokenId1, GroupListResponse group) {
        ResponseValidator.verifyForbiddenOperation(

            GroupActions.getDeleteGroupResponse(getServerPort(), accessTokenId1, group.getGroupId())
        );
    }

    private static void deleteGroup(UUID accessTokenId1, UUID accessTokenId2, GroupListResponse group) {
        GroupActions.deleteGroup(getServerPort(), accessTokenId2, group.getGroupId());

        assertThat(GroupActions.getGroups(getServerPort(), accessTokenId1)).isEmpty();
        assertThat(GroupActions.getGroups(getServerPort(), accessTokenId2)).isEmpty();
    }

    private GroupMemberResponse getOwnMember(UUID accessTokenId, UUID groupId, UUID userId) {
        return GroupActions.getMembers(getServerPort(), accessTokenId, groupId)
            .stream()
            .filter(groupMemberResponse -> groupMemberResponse.getUserId().equals(userId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("GroupMember not found for userId " + userId + " in group " + groupId));
    }
}
