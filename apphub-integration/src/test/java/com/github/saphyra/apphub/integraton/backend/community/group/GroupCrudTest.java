package com.github.saphyra.apphub.integraton.backend.community.group;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.community.CommunityActions;
import com.github.saphyra.apphub.integration.action.backend.community.GroupActions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.community.GroupInvitationType;
import com.github.saphyra.apphub.integration.structure.community.GroupListResponse;
import com.github.saphyra.apphub.integration.structure.community.GroupMemberResponse;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupCrudTest extends BackEndTest {
    private static final String GROUP_NAME = "group-name";
    private static final String NEW_GROUP_NAME = "new-group-name";

    @Test(dataProvider = "languageDataProvider", groups = "community")
    public void groupCrud(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(language, userData2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());

        CommunityActions.setUpFriendship(language, accessTokenId1, accessTokenId2, userId2);

        //Create - Name null
        ResponseValidator.verifyInvalidParam(
            language,
            GroupActions.getCreateGroupResponse(language, accessTokenId1, null),
            "groupName",
            "must not be null"
        );

        //Create - Name too short
        ResponseValidator.verifyInvalidParam(
            language,
            GroupActions.getCreateGroupResponse(language, accessTokenId1, "as"),
            "groupName",
            "too short"
        );

        //Create - Name too long
        ResponseValidator.verifyInvalidParam(
            language,
            GroupActions.getCreateGroupResponse(language, accessTokenId1, Stream.generate(() -> "a").limit(31).collect(Collectors.joining())),
            "groupName",
            "too long"
        );

        //Create
        GroupListResponse group = GroupActions.createGroup(language, accessTokenId1, GROUP_NAME);

        assertThat(group.getName()).isEqualTo(GROUP_NAME);
        assertThat(group.getOwnerId()).isEqualTo(userId1);
        assertThat(group.getInvitationType()).isEqualTo(GroupInvitationType.FRIENDS);

        //Rename - Name null
        ResponseValidator.verifyInvalidParam(
            language,
            GroupActions.getRenameGroupResponse(language, accessTokenId1, group.getGroupId(), null),
            "groupName",
            "must not be null"
        );

        //Rename - Name too short
        ResponseValidator.verifyInvalidParam(
            language,
            GroupActions.getRenameGroupResponse(language, accessTokenId1, group.getGroupId(), "as"),
            "groupName",
            "too short"
        );

        //Rename - Name too long
        ResponseValidator.verifyInvalidParam(
            language,
            GroupActions.getRenameGroupResponse(language, accessTokenId1, group.getGroupId(), Stream.generate(() -> "a").limit(31).collect(Collectors.joining())),
            "groupName",
            "too long"
        );

        //Rename - Not owner
        GroupMemberResponse groupMember = GroupActions.createMember(language, accessTokenId1, group.getGroupId(), userId2);
        ResponseValidator.verifyForbiddenOperation(
            language,
            GroupActions.getRenameGroupResponse(language, accessTokenId2, group.getGroupId(), NEW_GROUP_NAME)
        );

        //Rename
        group = GroupActions.renameGroup(language, accessTokenId1, group.getGroupId(), NEW_GROUP_NAME);

        assertThat(group.getName()).isEqualTo(NEW_GROUP_NAME);

        //Change invitationType - null
        ResponseValidator.verifyInvalidParam(
            language,
            GroupActions.getChangeInvitationTypeResponse(language, accessTokenId1, group.getGroupId(), null),
            "invitationType",
            "must not be null"
        );

        //Change invitationType - Not owner
        ResponseValidator.verifyForbiddenOperation(
            language,
            GroupActions.getChangeInvitationTypeResponse(language, accessTokenId2, group.getGroupId(), GroupInvitationType.FRIENDS_OF_FRIENDS)
        );

        //Change invitationType
        group = GroupActions.changeInvitationType(language, accessTokenId1, group.getGroupId(), GroupInvitationType.FRIENDS_OF_FRIENDS);

        assertThat(group.getInvitationType()).isEqualTo(GroupInvitationType.FRIENDS_OF_FRIENDS);

        //Change owner - null
        ResponseValidator.verifyInvalidParam(
            language,
            GroupActions.getChangeOwnerResponse(language, accessTokenId1, group.getGroupId(), null),
            "groupMemberId",
            "must not be null"
        );

        //Change owner - Not owner
        ResponseValidator.verifyForbiddenOperation(
            language,
            GroupActions.getChangeOwnerResponse(language, accessTokenId2, group.getGroupId(), groupMember.getGroupMemberId())
        );

        //Change owner - Already owner
        ResponseValidator.verifyErrorResponse(
            language,
            GroupActions.getChangeOwnerResponse(language, accessTokenId1, group.getGroupId(), getOwnMember(language, accessTokenId1, group.getGroupId(), userId1).getGroupMemberId()),
            409,
            ErrorCode.GENERAL_ERROR
        );

        //Change owner
        GroupActions.changeOwner(language, accessTokenId1, group.getGroupId(), groupMember.getGroupMemberId());

        assertThat(GroupActions.getGroups(language, accessTokenId1).get(0).getOwnerId());

        //Delete group - Not owner
        ResponseValidator.verifyForbiddenOperation(
            language,
            GroupActions.getDeleteGroupResponse(language, accessTokenId1, group.getGroupId())
        );

        //Delete group
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
