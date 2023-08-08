package com.github.saphyra.apphub.integraton.backend.community.group;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.core.TestBase;
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
import com.github.saphyra.apphub.integration.structure.api.community.GroupMemberRoleRequest;
import com.github.saphyra.apphub.integration.structure.api.community.SearchResultItem;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupMemberCrudTest extends BackEndTest {
    private static final String GROUP_NAME = "group-name";

    @Test(dataProvider = "languageDataProvider", groups = "community")
    public void groupMemberCrud(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);
        UUID userId = DatabaseUtil.getUserIdByEmail(userData.getEmail());

        RegistrationParameters friendData1 = RegistrationParameters.validParameters();
        UUID friendAccessTokenId1 = IndexPageActions.registerAndLogin(language, friendData1);
        UUID friendUserId1 = DatabaseUtil.getUserIdByEmail(friendData1.getEmail());

        RegistrationParameters friendData2 = RegistrationParameters.validParameters();
        UUID friendAccessTokenId2 = IndexPageActions.registerAndLogin(language, friendData2);
        UUID friendUserId2 = DatabaseUtil.getUserIdByEmail(friendData2.getEmail());

        RegistrationParameters friendOfFriendData = RegistrationParameters.validParameters();
        UUID friendOfFriendAccessTokenId = IndexPageActions.registerAndLogin(language, friendOfFriendData);
        UUID friendOfFriendUserId = DatabaseUtil.getUserIdByEmail(friendOfFriendData.getEmail());

        CommunityActions.setUpFriendship(language, accessTokenId, friendAccessTokenId1, friendUserId1);
        CommunityActions.setUpFriendship(language, accessTokenId, friendAccessTokenId2, friendUserId2);
        CommunityActions.setUpFriendship(language, friendAccessTokenId1, friendOfFriendAccessTokenId, friendOfFriendUserId);

        GroupListResponse group = GroupActions.createGroup(language, accessTokenId, GROUP_NAME);

        //Search - Friends only - No members
        List<SearchResultItem> searchResult = GroupActions.search(language, accessTokenId, group.getGroupId(), TestBase.getEmailDomain());

        assertThat(searchResult.stream().map(SearchResultItem::getUserId)).containsExactlyInAnyOrder(friendUserId1, friendUserId2);

        //Create - null memberUserId
        ResponseValidator.verifyInvalidParam(
            language,
            GroupActions.getCreateGroupMemberResponse(language, accessTokenId, group.getGroupId(), null),
            "memberUserId",
            "must not be null"
        );

        //Create - Cannot be added
        ResponseValidator.verifyErrorResponse(
            language,
            GroupActions.getCreateGroupMemberResponse(language, accessTokenId, group.getGroupId(), friendOfFriendUserId),
            409,
            ErrorCode.GENERAL_ERROR
        );

        //Create
        GroupMemberResponse groupMember = GroupActions.createMember(language, accessTokenId, group.getGroupId(), friendUserId1);

        assertThat(groupMember.getUserId()).isEqualTo(friendUserId1);
        assertThat(groupMember.getUsername()).isEqualTo(friendData1.getUsername());
        assertThat(groupMember.getEmail()).isEqualTo(friendData1.getEmail());
        assertThat(groupMember.getCanInvite()).isFalse();
        assertThat(groupMember.getCanKick()).isFalse();
        assertThat(groupMember.getCanModifyRoles()).isFalse();

        //Create - Already member
        ResponseValidator.verifyErrorResponse(
            language,
            GroupActions.getCreateGroupMemberResponse(language, accessTokenId, group.getGroupId(), friendUserId1),
            409,
            ErrorCode.GENERAL_ERROR
        );

        //Search - Member not in list
        searchResult = GroupActions.search(language, accessTokenId, group.getGroupId(), TestBase.getEmailDomain());

        assertThat(searchResult.stream().map(SearchResultItem::getUserId)).containsExactlyInAnyOrder(friendUserId2);

        //Search - Friend of friend should appear
        GroupActions.changeInvitationType(language, accessTokenId, group.getGroupId(), GroupInvitationType.FRIENDS_OF_FRIENDS);

        searchResult = GroupActions.search(language, accessTokenId, group.getGroupId(), TestBase.getEmailDomain());

        assertThat(searchResult.stream().map(SearchResultItem::getUserId)).containsExactlyInAnyOrder(friendUserId2, friendOfFriendUserId);

        //Create - No role
        ResponseValidator.verifyForbiddenOperation(
            language,
            GroupActions.getCreateGroupMemberResponse(language, friendAccessTokenId1, group.getGroupId(), friendOfFriendUserId)
        );

        //Modify roles - null canInvite
        ResponseValidator.verifyInvalidParam(
            language,
            GroupActions.getModifyRolesResponse(language, accessTokenId, group.getGroupId(), groupMember.getGroupMemberId(), new GroupMemberRoleRequest(null, false, false)),
            "canInvite",
            "must not be null"
        );

        //Modify roles - null canKick
        ResponseValidator.verifyInvalidParam(
            language,
            GroupActions.getModifyRolesResponse(language, accessTokenId, group.getGroupId(), groupMember.getGroupMemberId(), new GroupMemberRoleRequest(false, null, false)),
            "canKick",
            "must not be null"
        );

        //Modify roles - null canModifyRoles
        ResponseValidator.verifyInvalidParam(
            language,
            GroupActions.getModifyRolesResponse(language, accessTokenId, group.getGroupId(), groupMember.getGroupMemberId(), new GroupMemberRoleRequest(false, false, null)),
            "canModifyRoles",
            "must not be null"
        );

        //Modify roles - can invite
        groupMember = GroupActions.modifyRoles(language, accessTokenId, group.getGroupId(), groupMember.getGroupMemberId(), new GroupMemberRoleRequest(true, false, false));

        assertThat(groupMember.getCanInvite()).isTrue();
        assertThat(groupMember.getCanKick()).isFalse();
        assertThat(groupMember.getCanModifyRoles()).isFalse();

        //Create by member
        UUID friendOfFriendGroupMemberId = GroupActions.createMember(language, friendAccessTokenId1, group.getGroupId(), friendOfFriendUserId)
            .getGroupMemberId();

        //Delete - no role
        ResponseValidator.verifyForbiddenOperation(
            language,
            GroupActions.getDeleteGroupMemberResponse(language, friendAccessTokenId1, group.getGroupId(), friendOfFriendGroupMemberId)
        );

        //Delete - own
        GroupActions.deleteGroupMember(language, friendOfFriendAccessTokenId, group.getGroupId(), friendOfFriendGroupMemberId);

        assertThat(GroupActions.getMembers(language, accessTokenId, group.getGroupId()).stream().map(GroupMemberResponse::getUserId)).containsExactlyInAnyOrder(userId, friendUserId1);

        //Delete - owner
        ResponseValidator.verifyForbiddenOperation(
            language,
            GroupActions.getDeleteGroupMemberResponse(language, accessTokenId, group.getGroupId(), getOwnMember(language, accessTokenId, group.getGroupId(), userId).getGroupMemberId())
        );

        //Modify roles - canKick
        groupMember = GroupActions.modifyRoles(language, accessTokenId, group.getGroupId(), groupMember.getGroupMemberId(), new GroupMemberRoleRequest(false, true, false));

        assertThat(groupMember.getCanInvite()).isFalse();
        assertThat(groupMember.getCanKick()).isTrue();
        assertThat(groupMember.getCanModifyRoles()).isFalse();

        //Delete
        GroupActions.createMember(language, accessTokenId, group.getGroupId(), friendUserId2);

        GroupActions.deleteGroupMember(language, friendAccessTokenId1, group.getGroupId(), getOwnMember(language, accessTokenId, group.getGroupId(), friendUserId2).getGroupMemberId());

        //Modify roles - no role
        UUID friend2GroupMemberId = GroupActions.createMember(language, accessTokenId, group.getGroupId(), friendUserId2)
            .getGroupMemberId();

        ResponseValidator.verifyForbiddenOperation(
            language,
            GroupActions.getModifyRolesResponse(language, friendAccessTokenId1, group.getGroupId(), friend2GroupMemberId, new GroupMemberRoleRequest(true, true, true))
        );

        //Modify roles - canModifyRoles
        groupMember = GroupActions.modifyRoles(language, accessTokenId, group.getGroupId(), groupMember.getGroupMemberId(), new GroupMemberRoleRequest(false, false, true));

        assertThat(groupMember.getCanInvite()).isFalse();
        assertThat(groupMember.getCanKick()).isFalse();
        assertThat(groupMember.getCanModifyRoles()).isTrue();

        //Modify roles
        GroupMemberResponse friend2GroupMember = GroupActions.modifyRoles(language, friendAccessTokenId1, group.getGroupId(), friend2GroupMemberId, new GroupMemberRoleRequest(true, true, true));

        assertThat(friend2GroupMember.getCanInvite()).isTrue();
        assertThat(friend2GroupMember.getCanKick()).isTrue();
        assertThat(friend2GroupMember.getCanModifyRoles()).isTrue();
    }

    private GroupMemberResponse getOwnMember(Language language, UUID accessTokenId, UUID groupId, UUID userId) {
        return GroupActions.getMembers(language, accessTokenId, groupId)
            .stream()
            .filter(groupMemberResponse -> groupMemberResponse.getUserId().equals(userId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("GroupMember not found for userId " + userId + " in group " + groupId));
    }
}
