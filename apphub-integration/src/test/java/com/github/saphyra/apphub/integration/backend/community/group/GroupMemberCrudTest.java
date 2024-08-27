package com.github.saphyra.apphub.integration.backend.community.group;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.community.CommunityActions;
import com.github.saphyra.apphub.integration.action.backend.community.GroupActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.core.TestBase;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
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

    @Test(groups = {"be", "community"})
    public void groupMemberCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);
        UUID userId = DatabaseUtil.getUserIdByEmail(userData.getEmail());

        RegistrationParameters friendData1 = RegistrationParameters.validParameters();
        UUID friendAccessTokenId1 = IndexPageActions.registerAndLogin(getServerPort(), friendData1);
        UUID friendUserId1 = DatabaseUtil.getUserIdByEmail(friendData1.getEmail());

        RegistrationParameters friendData2 = RegistrationParameters.validParameters();
        UUID friendAccessTokenId2 = IndexPageActions.registerAndLogin(getServerPort(), friendData2);
        UUID friendUserId2 = DatabaseUtil.getUserIdByEmail(friendData2.getEmail());

        RegistrationParameters friendOfFriendData = RegistrationParameters.validParameters();
        UUID friendOfFriendAccessTokenId = IndexPageActions.registerAndLogin(getServerPort(), friendOfFriendData);
        UUID friendOfFriendUserId = DatabaseUtil.getUserIdByEmail(friendOfFriendData.getEmail());

        CommunityActions.setUpFriendship(getServerPort(), accessTokenId, friendAccessTokenId1, friendUserId1);
        CommunityActions.setUpFriendship(getServerPort(), accessTokenId, friendAccessTokenId2, friendUserId2);
        CommunityActions.setUpFriendship(getServerPort(), friendAccessTokenId1, friendOfFriendAccessTokenId, friendOfFriendUserId);

        GroupListResponse group = GroupActions.createGroup(getServerPort(), accessTokenId, GROUP_NAME);

        search_friendsOnly_noMembers(accessTokenId, friendUserId1, friendUserId2, group);
        create_nullMemberUserId(accessTokenId, group);
        create_cannotBeAdded(accessTokenId, friendOfFriendUserId, group);
        GroupMemberResponse groupMember = create(accessTokenId, friendData1, friendUserId1, group);
        create_alreadyMember(accessTokenId, friendUserId1, group);
        search_memberNotInList(accessTokenId, friendUserId2, group);
        search_friendOfFriendShouldAppear(accessTokenId, friendUserId2, friendOfFriendUserId, group);
        create_noRole(friendAccessTokenId1, friendOfFriendUserId, group);
        modifyRoles_nullCanInvite(accessTokenId, group, groupMember);
        modifyRoles_nullCanKick(accessTokenId, group, groupMember);
        modifyRoles_nullCanModifyRoles(accessTokenId, group, groupMember);
        groupMember = modifyRoles_canInvite(accessTokenId, group, groupMember);
        UUID friendOfFriendGroupMemberId = createByMember(friendAccessTokenId1, friendOfFriendUserId, group);
        delete_noRole(friendAccessTokenId1, group, friendOfFriendGroupMemberId);
        delete_own(accessTokenId, userId, friendUserId1, friendOfFriendAccessTokenId, group, friendOfFriendGroupMemberId);
        delete_owner(accessTokenId, userId, group);
        groupMember = modifyRoles_canKick(accessTokenId, group, groupMember);
        delete(accessTokenId, friendAccessTokenId1, friendUserId2, group);
        UUID friend2GroupMemberId = modifyRoles_noRole(accessTokenId, friendAccessTokenId1, friendUserId2, group);
        modifyRoles_canModifyRoles(accessTokenId, group, groupMember);
        modifyRoles(friendAccessTokenId1, group, friend2GroupMemberId);
    }

    private static GroupMemberResponse create(UUID accessTokenId, RegistrationParameters friendData1, UUID friendUserId1, GroupListResponse group) {
        GroupMemberResponse groupMember = GroupActions.createMember(getServerPort(), accessTokenId, group.getGroupId(), friendUserId1);
        assertThat(groupMember.getUsername()).isEqualTo(friendData1.getUsername());
        assertThat(groupMember.getEmail()).isEqualTo(friendData1.getEmail());
        assertThat(groupMember.getCanInvite()).isFalse();
        assertThat(groupMember.getCanKick()).isFalse();
        assertThat(groupMember.getCanModifyRoles()).isFalse();
        return groupMember;
    }

    private static void search_friendsOnly_noMembers(UUID accessTokenId, UUID friendUserId1, UUID friendUserId2, GroupListResponse group) {
        List<SearchResultItem> searchResult = GroupActions.search(getServerPort(), accessTokenId, group.getGroupId(), TestBase.getEmailDomain());

        assertThat(searchResult.stream().map(SearchResultItem::getUserId)).containsExactlyInAnyOrder(friendUserId1, friendUserId2);
    }

    private static void create_nullMemberUserId(UUID accessTokenId, GroupListResponse group) {
        ResponseValidator.verifyInvalidParam(

            GroupActions.getCreateGroupMemberResponse(getServerPort(), accessTokenId, group.getGroupId(), null),
            "memberUserId",
            "must not be null"
        );
    }

    private static void create_cannotBeAdded(UUID accessTokenId, UUID friendOfFriendUserId, GroupListResponse group) {
        ResponseValidator.verifyErrorResponse(

            GroupActions.getCreateGroupMemberResponse(getServerPort(), accessTokenId, group.getGroupId(), friendOfFriendUserId),
            409,
            ErrorCode.GENERAL_ERROR
        );
    }

    private static void create_alreadyMember(UUID accessTokenId, UUID friendUserId1, GroupListResponse group) {
        ResponseValidator.verifyErrorResponse(

            GroupActions.getCreateGroupMemberResponse(getServerPort(), accessTokenId, group.getGroupId(), friendUserId1),
            409,
            ErrorCode.GENERAL_ERROR
        );
    }

    private static void search_memberNotInList(UUID accessTokenId, UUID friendUserId2, GroupListResponse group) {
        List<SearchResultItem> searchResult;
        searchResult = GroupActions.search(getServerPort(), accessTokenId, group.getGroupId(), TestBase.getEmailDomain());

        assertThat(searchResult.stream().map(SearchResultItem::getUserId)).containsExactlyInAnyOrder(friendUserId2);
    }

    private static void search_friendOfFriendShouldAppear(UUID accessTokenId, UUID friendUserId2, UUID friendOfFriendUserId, GroupListResponse group) {
        List<SearchResultItem> searchResult;
        GroupActions.changeInvitationType(getServerPort(), accessTokenId, group.getGroupId(), GroupInvitationType.FRIENDS_OF_FRIENDS);

        searchResult = GroupActions.search(getServerPort(), accessTokenId, group.getGroupId(), TestBase.getEmailDomain());

        assertThat(searchResult.stream().map(SearchResultItem::getUserId)).containsExactlyInAnyOrder(friendUserId2, friendOfFriendUserId);
    }

    private static void create_noRole(UUID friendAccessTokenId1, UUID friendOfFriendUserId, GroupListResponse group) {
        ResponseValidator.verifyForbiddenOperation(

            GroupActions.getCreateGroupMemberResponse(getServerPort(), friendAccessTokenId1, group.getGroupId(), friendOfFriendUserId)
        );
    }

    private static void modifyRoles_nullCanInvite(UUID accessTokenId, GroupListResponse group, GroupMemberResponse groupMember) {
        ResponseValidator.verifyInvalidParam(

            GroupActions.getModifyRolesResponse(getServerPort(), accessTokenId, group.getGroupId(), groupMember.getGroupMemberId(), new GroupMemberRoleRequest(null, false, false)),
            "canInvite",
            "must not be null"
        );
    }

    private static void modifyRoles_nullCanKick(UUID accessTokenId, GroupListResponse group, GroupMemberResponse groupMember) {
        ResponseValidator.verifyInvalidParam(

            GroupActions.getModifyRolesResponse(getServerPort(), accessTokenId, group.getGroupId(), groupMember.getGroupMemberId(), new GroupMemberRoleRequest(false, null, false)),
            "canKick",
            "must not be null"
        );
    }

    private static void modifyRoles_nullCanModifyRoles(UUID accessTokenId, GroupListResponse group, GroupMemberResponse groupMember) {
        ResponseValidator.verifyInvalidParam(

            GroupActions.getModifyRolesResponse(getServerPort(), accessTokenId, group.getGroupId(), groupMember.getGroupMemberId(), new GroupMemberRoleRequest(false, false, null)),
            "canModifyRoles",
            "must not be null"
        );
    }

    private static GroupMemberResponse modifyRoles_canInvite(UUID accessTokenId, GroupListResponse group, GroupMemberResponse groupMember) {
        groupMember = GroupActions.modifyRoles(getServerPort(), accessTokenId, group.getGroupId(), groupMember.getGroupMemberId(), new GroupMemberRoleRequest(true, false, false));

        assertThat(groupMember.getCanInvite()).isTrue();
        assertThat(groupMember.getCanKick()).isFalse();
        assertThat(groupMember.getCanModifyRoles()).isFalse();
        return groupMember;
    }

    private static UUID createByMember(UUID friendAccessTokenId1, UUID friendOfFriendUserId, GroupListResponse group) {
        return GroupActions.createMember(getServerPort(), friendAccessTokenId1, group.getGroupId(), friendOfFriendUserId)
            .getGroupMemberId();
    }

    private static void delete_noRole(UUID friendAccessTokenId1, GroupListResponse group, UUID friendOfFriendGroupMemberId) {
        ResponseValidator.verifyForbiddenOperation(

            GroupActions.getDeleteGroupMemberResponse(getServerPort(), friendAccessTokenId1, group.getGroupId(), friendOfFriendGroupMemberId)
        );
    }

    private static void delete_own(UUID accessTokenId, UUID userId, UUID friendUserId1, UUID friendOfFriendAccessTokenId, GroupListResponse group, UUID friendOfFriendGroupMemberId) {
        GroupActions.deleteGroupMember(getServerPort(), friendOfFriendAccessTokenId, group.getGroupId(), friendOfFriendGroupMemberId);

        assertThat(GroupActions.getMembers(getServerPort(), accessTokenId, group.getGroupId()).stream().map(GroupMemberResponse::getUserId)).containsExactlyInAnyOrder(userId, friendUserId1);
    }

    private void delete_owner(UUID accessTokenId, UUID userId, GroupListResponse group) {
        ResponseValidator.verifyForbiddenOperation(

            GroupActions.getDeleteGroupMemberResponse(getServerPort(), accessTokenId, group.getGroupId(), getOwnMember(accessTokenId, group.getGroupId(), userId).getGroupMemberId())
        );
    }

    private static GroupMemberResponse modifyRoles_canKick(UUID accessTokenId, GroupListResponse group, GroupMemberResponse groupMember) {
        groupMember = GroupActions.modifyRoles(getServerPort(), accessTokenId, group.getGroupId(), groupMember.getGroupMemberId(), new GroupMemberRoleRequest(false, true, false));

        assertThat(groupMember.getCanInvite()).isFalse();
        assertThat(groupMember.getCanKick()).isTrue();
        assertThat(groupMember.getCanModifyRoles()).isFalse();
        return groupMember;
    }

    private void delete(UUID accessTokenId, UUID friendAccessTokenId1, UUID friendUserId2, GroupListResponse group) {
        GroupActions.createMember(getServerPort(), accessTokenId, group.getGroupId(), friendUserId2);

        GroupActions.deleteGroupMember(getServerPort(), friendAccessTokenId1, group.getGroupId(), getOwnMember(accessTokenId, group.getGroupId(), friendUserId2).getGroupMemberId());
    }

    private static UUID modifyRoles_noRole(UUID accessTokenId, UUID friendAccessTokenId1, UUID friendUserId2, GroupListResponse group) {
        UUID friend2GroupMemberId = GroupActions.createMember(getServerPort(), accessTokenId, group.getGroupId(), friendUserId2)
            .getGroupMemberId();

        ResponseValidator.verifyForbiddenOperation(

            GroupActions.getModifyRolesResponse(getServerPort(), friendAccessTokenId1, group.getGroupId(), friend2GroupMemberId, new GroupMemberRoleRequest(true, true, true))
        );
        return friend2GroupMemberId;
    }

    private static void modifyRoles_canModifyRoles(UUID accessTokenId, GroupListResponse group, GroupMemberResponse groupMember) {
        groupMember = GroupActions.modifyRoles(getServerPort(), accessTokenId, group.getGroupId(), groupMember.getGroupMemberId(), new GroupMemberRoleRequest(false, false, true));

        assertThat(groupMember.getCanInvite()).isFalse();
        assertThat(groupMember.getCanKick()).isFalse();
        assertThat(groupMember.getCanModifyRoles()).isTrue();
    }

    private static void modifyRoles(UUID friendAccessTokenId1, GroupListResponse group, UUID friend2GroupMemberId) {
        GroupMemberResponse friend2GroupMember = GroupActions.modifyRoles(getServerPort(), friendAccessTokenId1, group.getGroupId(), friend2GroupMemberId, new GroupMemberRoleRequest(true, true, true));

        assertThat(friend2GroupMember.getCanInvite()).isTrue();
        assertThat(friend2GroupMember.getCanKick()).isTrue();
        assertThat(friend2GroupMember.getCanModifyRoles()).isTrue();
    }

    private GroupMemberResponse getOwnMember(UUID accessTokenId, UUID groupId, UUID userId) {
        return GroupActions.getMembers(getServerPort(), accessTokenId, groupId)
            .stream()
            .filter(groupMemberResponse -> groupMemberResponse.getUserId().equals(userId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("GroupMember not found for userId " + userId + " in group " + groupId));
    }
}
