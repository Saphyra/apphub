package com.github.saphyra.apphub.integration.backend.community.group;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.community.CommunityActions;
import com.github.saphyra.apphub.integration.action.backend.community.GroupActions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
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

    @Test(dataProvider = "languageDataProvider", groups = {"be", "community"})
    public void groupCrud(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(language, userData2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());

        CommunityActions.setUpFriendship(language, accessTokenId1, accessTokenId2, userId2);

        create_nameNull(language, accessTokenId1);
        rename_nameTooShort(language, accessTokenId1);
        create_nameTooLong(language, accessTokenId1);
        GroupListResponse group = create(language, accessTokenId1, userId1);
        rename_nameNull(language, accessTokenId1, group);
        rename_nameTooShort(language, accessTokenId1, group);
        rename_nameTooLong(language, accessTokenId1, group);
        GroupMemberResponse groupMember = rename_notOwner(language, accessTokenId1, accessTokenId2, userId2, group);
        group = rename(language, accessTokenId1, group);
        changeInvitationType_null(language, accessTokenId1, group);
        changeInvitationType_notOwner(language, accessTokenId2, group);
        group = changeInvitationType(language, accessTokenId1, group);
        changeOwner_null(language, accessTokenId1, group);
        changeOwner_notOwner(language, accessTokenId2, group, groupMember);
        changeOwner_alreadyOwner(language, accessTokenId1, userId1, group);
        changeOwner(language, accessTokenId1, group, groupMember);
        deleteGroup_notOwner(language, accessTokenId1, group);
        deleteGroup(language, accessTokenId1, accessTokenId2, group);
    }

    private static void create_nameNull(Language language, UUID accessTokenId1) {
        ResponseValidator.verifyInvalidParam(
            language,
            GroupActions.getCreateGroupResponse(language, accessTokenId1, null),
            "groupName",
            "must not be null"
        );
    }

    private static void rename_nameTooShort(Language language, UUID accessTokenId1) {
        ResponseValidator.verifyInvalidParam(
            language,
            GroupActions.getCreateGroupResponse(language, accessTokenId1, "as"),
            "groupName",
            "too short"
        );
    }

    private static void create_nameTooLong(Language language, UUID accessTokenId1) {
        ResponseValidator.verifyInvalidParam(
            language,
            GroupActions.getCreateGroupResponse(language, accessTokenId1, Stream.generate(() -> "a").limit(31).collect(Collectors.joining())),
            "groupName",
            "too long"
        );
    }

    private static GroupListResponse create(Language language, UUID accessTokenId1, UUID userId1) {
        GroupListResponse group = GroupActions.createGroup(language, accessTokenId1, GROUP_NAME);

        assertThat(group.getName()).isEqualTo(GROUP_NAME);
        assertThat(group.getOwnerId()).isEqualTo(userId1);
        assertThat(group.getInvitationType()).isEqualTo(GroupInvitationType.FRIENDS);
        return group;
    }

    private static void rename_nameNull(Language language, UUID accessTokenId1, GroupListResponse group) {
        ResponseValidator.verifyInvalidParam(
            language,
            GroupActions.getRenameGroupResponse(language, accessTokenId1, group.getGroupId(), null),
            "groupName",
            "must not be null"
        );
    }

    private static void rename_nameTooShort(Language language, UUID accessTokenId1, GroupListResponse group) {
        ResponseValidator.verifyInvalidParam(
            language,
            GroupActions.getRenameGroupResponse(language, accessTokenId1, group.getGroupId(), "as"),
            "groupName",
            "too short"
        );
    }

    private static void rename_nameTooLong(Language language, UUID accessTokenId1, GroupListResponse group) {
        ResponseValidator.verifyInvalidParam(
            language,
            GroupActions.getRenameGroupResponse(language, accessTokenId1, group.getGroupId(), Stream.generate(() -> "a").limit(31).collect(Collectors.joining())),
            "groupName",
            "too long"
        );
    }

    private static GroupMemberResponse rename_notOwner(Language language, UUID accessTokenId1, UUID accessTokenId2, UUID userId2, GroupListResponse group) {
        GroupMemberResponse groupMember = GroupActions.createMember(language, accessTokenId1, group.getGroupId(), userId2);
        ResponseValidator.verifyForbiddenOperation(
            language,
            GroupActions.getRenameGroupResponse(language, accessTokenId2, group.getGroupId(), NEW_GROUP_NAME)
        );
        return groupMember;
    }

    private static GroupListResponse rename(Language language, UUID accessTokenId1, GroupListResponse group) {
        group = GroupActions.renameGroup(language, accessTokenId1, group.getGroupId(), NEW_GROUP_NAME);

        assertThat(group.getName()).isEqualTo(NEW_GROUP_NAME);
        return group;
    }

    private static void changeInvitationType_null(Language language, UUID accessTokenId1, GroupListResponse group) {
        ResponseValidator.verifyInvalidParam(
            language,
            GroupActions.getChangeInvitationTypeResponse(language, accessTokenId1, group.getGroupId(), null),
            "invitationType",
            "must not be null"
        );
    }

    private static void changeInvitationType_notOwner(Language language, UUID accessTokenId2, GroupListResponse group) {
        ResponseValidator.verifyForbiddenOperation(
            language,
            GroupActions.getChangeInvitationTypeResponse(language, accessTokenId2, group.getGroupId(), GroupInvitationType.FRIENDS_OF_FRIENDS)
        );
    }

    private static GroupListResponse changeInvitationType(Language language, UUID accessTokenId1, GroupListResponse group) {
        group = GroupActions.changeInvitationType(language, accessTokenId1, group.getGroupId(), GroupInvitationType.FRIENDS_OF_FRIENDS);

        assertThat(group.getInvitationType()).isEqualTo(GroupInvitationType.FRIENDS_OF_FRIENDS);
        return group;
    }

    private static void changeOwner_null(Language language, UUID accessTokenId1, GroupListResponse group) {
        ResponseValidator.verifyInvalidParam(
            language,
            GroupActions.getChangeOwnerResponse(language, accessTokenId1, group.getGroupId(), null),
            "groupMemberId",
            "must not be null"
        );
    }

    private static void changeOwner_notOwner(Language language, UUID accessTokenId2, GroupListResponse group, GroupMemberResponse groupMember) {
        ResponseValidator.verifyForbiddenOperation(
            language,
            GroupActions.getChangeOwnerResponse(language, accessTokenId2, group.getGroupId(), groupMember.getGroupMemberId())
        );
    }

    private void changeOwner_alreadyOwner(Language language, UUID accessTokenId1, UUID userId1, GroupListResponse group) {
        ResponseValidator.verifyErrorResponse(
            language,
            GroupActions.getChangeOwnerResponse(language, accessTokenId1, group.getGroupId(), getOwnMember(language, accessTokenId1, group.getGroupId(), userId1).getGroupMemberId()),
            409,
            ErrorCode.GENERAL_ERROR
        );
    }

    private static void changeOwner(Language language, UUID accessTokenId1, GroupListResponse group, GroupMemberResponse groupMember) {
        GroupActions.changeOwner(language, accessTokenId1, group.getGroupId(), groupMember.getGroupMemberId());

        assertThat(GroupActions.getGroups(language, accessTokenId1).get(0).getOwnerId());
    }

    private static void deleteGroup_notOwner(Language language, UUID accessTokenId1, GroupListResponse group) {
        ResponseValidator.verifyForbiddenOperation(
            language,
            GroupActions.getDeleteGroupResponse(language, accessTokenId1, group.getGroupId())
        );
    }

    private static void deleteGroup(Language language, UUID accessTokenId1, UUID accessTokenId2, GroupListResponse group) {
        GroupActions.deleteGroup(language, accessTokenId2, group.getGroupId());

        assertThat(GroupActions.getGroups(language, accessTokenId1)).isEmpty();
        assertThat(GroupActions.getGroups(language, accessTokenId2)).isEmpty();
    }

    private GroupMemberResponse getOwnMember(Language language, UUID accessTokenId, UUID groupId, UUID userId) {
        return GroupActions.getMembers(language, accessTokenId, groupId)
            .stream()
            .filter(groupMemberResponse -> groupMemberResponse.getUserId().equals(userId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("GroupMember not found for userId " + userId + " in group " + groupId));
    }
}
