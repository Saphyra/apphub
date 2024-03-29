package com.github.saphyra.apphub.integration.action.backend.community;

import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.community.GroupInvitationType;
import com.github.saphyra.apphub.integration.structure.api.community.GroupListResponse;
import com.github.saphyra.apphub.integration.structure.api.community.GroupMemberResponse;
import com.github.saphyra.apphub.integration.structure.api.community.GroupMemberRoleRequest;
import com.github.saphyra.apphub.integration.structure.api.community.SearchResultItem;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupActions {
    public static GroupListResponse createGroup(UUID accessTokenId, String groupName) {
        Response response = getCreateGroupResponse(accessTokenId, groupName);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(GroupListResponse.class);
    }

    public static Response getCreateGroupResponse(UUID accessTokenId, String groupName) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(groupName))
            .put(UrlFactory.create(Endpoints.COMMUNITY_GROUP_CREATE));
    }

    public static GroupListResponse renameGroup(UUID accessTokenId, UUID groupId, String groupName) {
        Response response = getRenameGroupResponse(accessTokenId, groupId, groupName);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(GroupListResponse.class);
    }

    public static Response getRenameGroupResponse(UUID accessTokenId, UUID groupId, String groupName) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(groupName))
            .post(UrlFactory.create(Endpoints.COMMUNITY_GROUP_RENAME, "groupId", groupId));
    }

    public static GroupMemberResponse createMember(UUID accessTokenId, UUID groupId, UUID userId) {
        Response response = getCreateGroupMemberResponse(accessTokenId, groupId, userId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(GroupMemberResponse.class);
    }

    public static Response getCreateGroupMemberResponse(UUID accessTokenId, UUID groupId, UUID userId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(userId))
            .put(UrlFactory.create(Endpoints.COMMUNITY_GROUP_CREATE_MEMBER, "groupId", groupId));
    }

    public static GroupListResponse changeInvitationType(UUID accessTokenId, UUID groupId, GroupInvitationType invitationType) {
        Response response = getChangeInvitationTypeResponse(accessTokenId, groupId, invitationType);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(GroupListResponse.class);
    }

    public static Response getChangeInvitationTypeResponse(UUID accessTokenId, UUID groupId, GroupInvitationType invitationType) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(invitationType))
            .post(UrlFactory.create(Endpoints.COMMUNITY_GROUP_CHANGE_INVITATION_TYPE, "groupId", groupId));
    }

    public static void changeOwner(UUID accessTokenId, UUID groupId, UUID groupMemberId) {
        Response response = getChangeOwnerResponse(accessTokenId, groupId, groupMemberId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getChangeOwnerResponse(UUID accessTokenId, UUID groupId, UUID groupMemberId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(groupMemberId))
            .post(UrlFactory.create(Endpoints.COMMUNITY_GROUP_CHANGE_OWNER, "groupId", groupId));
    }

    public static List<GroupMemberResponse> getMembers(UUID accessTokenId, UUID groupId) {
        Response response = getMembersResponse(accessTokenId, groupId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(GroupMemberResponse[].class));
    }

    public static Response getMembersResponse(UUID accessTokenId, UUID groupId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.COMMUNITY_GROUP_GET_MEMBERS, "groupId", groupId));
    }

    public static List<GroupListResponse> getGroups(UUID accessTokenId) {
        Response response = getGroupsResponse(accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(GroupListResponse[].class));
    }

    public static Response getGroupsResponse(UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.COMMUNITY_GET_GROUPS));
    }

    public static void deleteGroup(UUID accessTokenId, UUID groupId) {
        Response response = getDeleteGroupResponse(accessTokenId, groupId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteGroupResponse(UUID accessTokenId, UUID groupId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.COMMUNITY_GROUP_DELETE, "groupId", groupId));
    }

    public static List<SearchResultItem> search(UUID accessTokenId, UUID groupId, String query) {
        Response response = getSearchResponse(accessTokenId, groupId, query);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(SearchResultItem[].class));
    }

    public static Response getSearchResponse(UUID accessTokenId, UUID groupId, String query) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(query))
            .post(UrlFactory.create(Endpoints.COMMUNITY_GROUP_SEARCH_MEMBER_CANDIDATES, "groupId", groupId));
    }

    public static GroupMemberResponse modifyRoles(UUID accessTokenId, UUID groupId, UUID groupMemberId, GroupMemberRoleRequest request) {
        Response response = getModifyRolesResponse(accessTokenId, groupId, groupMemberId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(GroupMemberResponse.class);
    }

    public static Response getModifyRolesResponse(UUID accessTokenId, UUID groupId, UUID groupMemberId, GroupMemberRoleRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .post(UrlFactory.create(Endpoints.COMMUNITY_GROUP_MEMBER_ROLES, CollectionUtils.toMap(new BiWrapper<>("groupId", groupId), new BiWrapper<>("groupMemberId", groupMemberId))));
    }

    public static void deleteGroupMember(UUID accessTokenId, UUID groupId, UUID groupMemberId) {
        Response response = getDeleteGroupMemberResponse(accessTokenId, groupId, groupMemberId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteGroupMemberResponse(UUID accessTokenId, UUID groupId, UUID groupMemberId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.COMMUNITY_GROUP_DELETE_MEMBER, CollectionUtils.toMap(new BiWrapper<>("groupId", groupId), new BiWrapper<>("groupMemberId", groupMemberId))));
    }
}
