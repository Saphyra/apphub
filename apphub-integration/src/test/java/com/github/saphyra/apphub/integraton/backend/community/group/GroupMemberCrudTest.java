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

    @Test(dataProvider = "languageDataProvider", groups = {"be", "community"})
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

        search_friendsOnly_noMembers(language, accessTokenId, friendUserId1, friendUserId2, group);
        create_nullMemberUserId(language, accessTokenId, group);
        create_cannotBeAdded(language, accessTokenId, friendOfFriendUserId, group);
        GroupMemberResponse groupMember = create(language, accessTokenId, friendData1, friendUserId1, group);
        create_alreadyMember(language, accessTokenId, friendUserId1, group);
        search_memberNotInList(language, accessTokenId, friendUserId2, group);
        search_friendOfFriendShouldAppear(language, accessTokenId, friendUserId2, friendOfFriendUserId, group);
        create_noRole(language, friendAccessTokenId1, friendOfFriendUserId, group);
        modifyRoles_nullCanInvite(language, accessTokenId, group, groupMember);
        modifyRoles_nullCanKick(language, accessTokenId, group, groupMember);
        modifyRoles_nullCanModifyRoles(language, accessTokenId, group, groupMember);
        groupMember = modifyRoles_canInvite(language, accessTokenId, group, groupMember);
        UUID friendOfFriendGroupMemberId = createByMember(language, friendAccessTokenId1, friendOfFriendUserId, group);
        delete_noRole(language, friendAccessTokenId1, group, friendOfFriendGroupMemberId);
        delete_own(language, accessTokenId, userId, friendUserId1, friendOfFriendAccessTokenId, group, friendOfFriendGroupMemberId);
        delete_owner(language, accessTokenId, userId, group);
        groupMember = modifyRoles_canKick(language, accessTokenId, group, groupMember);
        delete(language, accessTokenId, friendAccessTokenId1, friendUserId2, group);
        UUID friend2GroupMemberId = modifyRoles_noRole(language, accessTokenId, friendAccessTokenId1, friendUserId2, group);
        modifyRoles_canModifyRoles(language, accessTokenId, group, groupMember);
        modifyRoles(language, friendAccessTokenId1, group, friend2GroupMemberId);
    }

    private static GroupMemberResponse create(Language language, UUID accessTokenId, RegistrationParameters friendData1, UUID friendUserId1, GroupListResponse group) {
        GroupMemberResponse groupMember = GroupActions.createMember(language, accessTokenId, group.getGroupId(), friendUserId1);
        assertThat(groupMember.getUsername()).isEqualTo(friendData1.getUsername());
        assertThat(groupMember.getEmail()).isEqualTo(friendData1.getEmail());
        assertThat(groupMember.getCanInvite()).isFalse();
        assertThat(groupMember.getCanKick()).isFalse();
        assertThat(groupMember.getCanModifyRoles()).isFalse();
        return groupMember;
    }

    private static void search_friendsOnly_noMembers(Language language, UUID accessTokenId, UUID friendUserId1, UUID friendUserId2, GroupListResponse group) {
        List<SearchResultItem> searchResult = GroupActions.search(language, accessTokenId, group.getGroupId(), TestBase.getEmailDomain());

        assertThat(searchResult.stream().map(SearchResultItem::getUserId)).containsExactlyInAnyOrder(friendUserId1, friendUserId2);
    }

    private static void create_nullMemberUserId(Language language, UUID accessTokenId, GroupListResponse group) {
        ResponseValidator.verifyInvalidParam(
            language,
            GroupActions.getCreateGroupMemberResponse(language, accessTokenId, group.getGroupId(), null),
            "memberUserId",
            "must not be null"
        );
    }

    private static void create_cannotBeAdded(Language language, UUID accessTokenId, UUID friendOfFriendUserId, GroupListResponse group) {
        ResponseValidator.verifyErrorResponse(
            language,
            GroupActions.getCreateGroupMemberResponse(language, accessTokenId, group.getGroupId(), friendOfFriendUserId),
            409,
            ErrorCode.GENERAL_ERROR
        );
    }

    private static void create_alreadyMember(Language language, UUID accessTokenId, UUID friendUserId1, GroupListResponse group) {
        ResponseValidator.verifyErrorResponse(
            language,
            GroupActions.getCreateGroupMemberResponse(language, accessTokenId, group.getGroupId(), friendUserId1),
            409,
            ErrorCode.GENERAL_ERROR
        );
    }

    private static void search_memberNotInList(Language language, UUID accessTokenId, UUID friendUserId2, GroupListResponse group) {
        List<SearchResultItem> searchResult;
        searchResult = GroupActions.search(language, accessTokenId, group.getGroupId(), TestBase.getEmailDomain());

        assertThat(searchResult.stream().map(SearchResultItem::getUserId)).containsExactlyInAnyOrder(friendUserId2);
    }

    private static void search_friendOfFriendShouldAppear(Language language, UUID accessTokenId, UUID friendUserId2, UUID friendOfFriendUserId, GroupListResponse group) {
        List<SearchResultItem> searchResult;
        GroupActions.changeInvitationType(language, accessTokenId, group.getGroupId(), GroupInvitationType.FRIENDS_OF_FRIENDS);

        searchResult = GroupActions.search(language, accessTokenId, group.getGroupId(), TestBase.getEmailDomain());

        assertThat(searchResult.stream().map(SearchResultItem::getUserId)).containsExactlyInAnyOrder(friendUserId2, friendOfFriendUserId);
    }

    private static void create_noRole(Language language, UUID friendAccessTokenId1, UUID friendOfFriendUserId, GroupListResponse group) {
        ResponseValidator.verifyForbiddenOperation(
            language,
            GroupActions.getCreateGroupMemberResponse(language, friendAccessTokenId1, group.getGroupId(), friendOfFriendUserId)
        );
    }

    private static void modifyRoles_nullCanInvite(Language language, UUID accessTokenId, GroupListResponse group, GroupMemberResponse groupMember) {
        ResponseValidator.verifyInvalidParam(
            language,
            GroupActions.getModifyRolesResponse(language, accessTokenId, group.getGroupId(), groupMember.getGroupMemberId(), new GroupMemberRoleRequest(null, false, false)),
            "canInvite",
            "must not be null"
        );
    }

    private static void modifyRoles_nullCanKick(Language language, UUID accessTokenId, GroupListResponse group, GroupMemberResponse groupMember) {
        ResponseValidator.verifyInvalidParam(
            language,
            GroupActions.getModifyRolesResponse(language, accessTokenId, group.getGroupId(), groupMember.getGroupMemberId(), new GroupMemberRoleRequest(false, null, false)),
            "canKick",
            "must not be null"
        );
    }

    private static void modifyRoles_nullCanModifyRoles(Language language, UUID accessTokenId, GroupListResponse group, GroupMemberResponse groupMember) {
        ResponseValidator.verifyInvalidParam(
            language,
            GroupActions.getModifyRolesResponse(language, accessTokenId, group.getGroupId(), groupMember.getGroupMemberId(), new GroupMemberRoleRequest(false, false, null)),
            "canModifyRoles",
            "must not be null"
        );
    }

    private static GroupMemberResponse modifyRoles_canInvite(Language language, UUID accessTokenId, GroupListResponse group, GroupMemberResponse groupMember) {
        groupMember = GroupActions.modifyRoles(language, accessTokenId, group.getGroupId(), groupMember.getGroupMemberId(), new GroupMemberRoleRequest(true, false, false));

        assertThat(groupMember.getCanInvite()).isTrue();
        assertThat(groupMember.getCanKick()).isFalse();
        assertThat(groupMember.getCanModifyRoles()).isFalse();
        return groupMember;
    }

    private static UUID createByMember(Language language, UUID friendAccessTokenId1, UUID friendOfFriendUserId, GroupListResponse group) {
        UUID friendOfFriendGroupMemberId = GroupActions.createMember(language, friendAccessTokenId1, group.getGroupId(), friendOfFriendUserId)
            .getGroupMemberId();
        return friendOfFriendGroupMemberId;
    }

    private static void delete_noRole(Language language, UUID friendAccessTokenId1, GroupListResponse group, UUID friendOfFriendGroupMemberId) {
        ResponseValidator.verifyForbiddenOperation(
            language,
            GroupActions.getDeleteGroupMemberResponse(language, friendAccessTokenId1, group.getGroupId(), friendOfFriendGroupMemberId)
        );
    }

    private static void delete_own(Language language, UUID accessTokenId, UUID userId, UUID friendUserId1, UUID friendOfFriendAccessTokenId, GroupListResponse group, UUID friendOfFriendGroupMemberId) {
        GroupActions.deleteGroupMember(language, friendOfFriendAccessTokenId, group.getGroupId(), friendOfFriendGroupMemberId);

        assertThat(GroupActions.getMembers(language, accessTokenId, group.getGroupId()).stream().map(GroupMemberResponse::getUserId)).containsExactlyInAnyOrder(userId, friendUserId1);
    }

    private void delete_owner(Language language, UUID accessTokenId, UUID userId, GroupListResponse group) {
        ResponseValidator.verifyForbiddenOperation(
            language,
            GroupActions.getDeleteGroupMemberResponse(language, accessTokenId, group.getGroupId(), getOwnMember(language, accessTokenId, group.getGroupId(), userId).getGroupMemberId())
        );
    }

    private static GroupMemberResponse modifyRoles_canKick(Language language, UUID accessTokenId, GroupListResponse group, GroupMemberResponse groupMember) {
        groupMember = GroupActions.modifyRoles(language, accessTokenId, group.getGroupId(), groupMember.getGroupMemberId(), new GroupMemberRoleRequest(false, true, false));

        assertThat(groupMember.getCanInvite()).isFalse();
        assertThat(groupMember.getCanKick()).isTrue();
        assertThat(groupMember.getCanModifyRoles()).isFalse();
        return groupMember;
    }

    private void delete(Language language, UUID accessTokenId, UUID friendAccessTokenId1, UUID friendUserId2, GroupListResponse group) {
        GroupActions.createMember(language, accessTokenId, group.getGroupId(), friendUserId2);

        GroupActions.deleteGroupMember(language, friendAccessTokenId1, group.getGroupId(), getOwnMember(language, accessTokenId, group.getGroupId(), friendUserId2).getGroupMemberId());
    }

    private static UUID modifyRoles_noRole(Language language, UUID accessTokenId, UUID friendAccessTokenId1, UUID friendUserId2, GroupListResponse group) {
        UUID friend2GroupMemberId = GroupActions.createMember(language, accessTokenId, group.getGroupId(), friendUserId2)
            .getGroupMemberId();

        ResponseValidator.verifyForbiddenOperation(
            language,
            GroupActions.getModifyRolesResponse(language, friendAccessTokenId1, group.getGroupId(), friend2GroupMemberId, new GroupMemberRoleRequest(true, true, true))
        );
        return friend2GroupMemberId;
    }

    private static void modifyRoles_canModifyRoles(Language language, UUID accessTokenId, GroupListResponse group, GroupMemberResponse groupMember) {
        groupMember = GroupActions.modifyRoles(language, accessTokenId, group.getGroupId(), groupMember.getGroupMemberId(), new GroupMemberRoleRequest(false, false, true));

        assertThat(groupMember.getCanInvite()).isFalse();
        assertThat(groupMember.getCanKick()).isFalse();
        assertThat(groupMember.getCanModifyRoles()).isTrue();
    }

    private static void modifyRoles(Language language, UUID friendAccessTokenId1, GroupListResponse group, UUID friend2GroupMemberId) {
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
