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
    public static GroupListResponse createGroup(int serverPort, UUID accessTokenId, String groupName) {
        Response response = getCreateGroupResponse(serverPort, accessTokenId, groupName);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(GroupListResponse.class);
    }

    public static Response getCreateGroupResponse(int serverPort, UUID accessTokenId, String groupName) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(groupName))
            .put(UrlFactory.create(serverPort, Endpoints.COMMUNITY_GROUP_CREATE));
    }

    public static GroupListResponse renameGroup(int serverPort, UUID accessTokenId, UUID groupId, String groupName) {
        Response response = getRenameGroupResponse(serverPort, accessTokenId, groupId, groupName);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(GroupListResponse.class);
    }

    public static Response getRenameGroupResponse(int serverPort, UUID accessTokenId, UUID groupId, String groupName) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(groupName))
            .post(UrlFactory.create(serverPort, Endpoints.COMMUNITY_GROUP_RENAME, "groupId", groupId));
    }

    public static GroupMemberResponse createMember(int serverPort, UUID accessTokenId, UUID groupId, UUID userId) {
        Response response = getCreateGroupMemberResponse(serverPort, accessTokenId, groupId, userId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(GroupMemberResponse.class);
    }

    public static Response getCreateGroupMemberResponse(int serverPort, UUID accessTokenId, UUID groupId, UUID userId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(userId))
            .put(UrlFactory.create(serverPort, Endpoints.COMMUNITY_GROUP_CREATE_MEMBER, "groupId", groupId));
    }

    public static GroupListResponse changeInvitationType(int serverPort, UUID accessTokenId, UUID groupId, GroupInvitationType invitationType) {
        Response response = getChangeInvitationTypeResponse(serverPort, accessTokenId, groupId, invitationType);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(GroupListResponse.class);
    }

    public static Response getChangeInvitationTypeResponse(int serverPort, UUID accessTokenId, UUID groupId, GroupInvitationType invitationType) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(invitationType))
            .post(UrlFactory.create(serverPort, Endpoints.COMMUNITY_GROUP_CHANGE_INVITATION_TYPE, "groupId", groupId));
    }

    public static void changeOwner(int serverPort, UUID accessTokenId, UUID groupId, UUID groupMemberId) {
        Response response = getChangeOwnerResponse(serverPort, accessTokenId, groupId, groupMemberId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getChangeOwnerResponse(int serverPort, UUID accessTokenId, UUID groupId, UUID groupMemberId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(groupMemberId))
            .post(UrlFactory.create(serverPort, Endpoints.COMMUNITY_GROUP_CHANGE_OWNER, "groupId", groupId));
    }

    public static List<GroupMemberResponse> getMembers(int serverPort, UUID accessTokenId, UUID groupId) {
        Response response = getMembersResponse(serverPort, accessTokenId, groupId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(GroupMemberResponse[].class));
    }

    public static Response getMembersResponse(int serverPort, UUID accessTokenId, UUID groupId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.COMMUNITY_GROUP_GET_MEMBERS, "groupId", groupId));
    }

    public static List<GroupListResponse> getGroups(int serverPort, UUID accessTokenId) {
        Response response = getGroupsResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(GroupListResponse[].class));
    }

    public static Response getGroupsResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.COMMUNITY_GET_GROUPS));
    }

    public static void deleteGroup(int serverPort, UUID accessTokenId, UUID groupId) {
        Response response = getDeleteGroupResponse(serverPort, accessTokenId, groupId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteGroupResponse(int serverPort, UUID accessTokenId, UUID groupId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, Endpoints.COMMUNITY_GROUP_DELETE, "groupId", groupId));
    }

    public static List<SearchResultItem> search(int serverPort, UUID accessTokenId, UUID groupId, String query) {
        Response response = getSearchResponse(serverPort, accessTokenId, groupId, query);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(SearchResultItem[].class));
    }

    public static Response getSearchResponse(int serverPort, UUID accessTokenId, UUID groupId, String query) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(query))
            .post(UrlFactory.create(serverPort, Endpoints.COMMUNITY_GROUP_SEARCH_MEMBER_CANDIDATES, "groupId", groupId));
    }

    public static GroupMemberResponse modifyRoles(int serverPort, UUID accessTokenId, UUID groupId, UUID groupMemberId, GroupMemberRoleRequest request) {
        Response response = getModifyRolesResponse(serverPort, accessTokenId, groupId, groupMemberId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(GroupMemberResponse.class);
    }

    public static Response getModifyRolesResponse(int serverPort, UUID accessTokenId, UUID groupId, UUID groupMemberId, GroupMemberRoleRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.COMMUNITY_GROUP_MEMBER_ROLES, CollectionUtils.toMap(new BiWrapper<>("groupId", groupId), new BiWrapper<>("groupMemberId", groupMemberId))));
    }

    public static void deleteGroupMember(int serverPort, UUID accessTokenId, UUID groupId, UUID groupMemberId) {
        Response response = getDeleteGroupMemberResponse(serverPort, accessTokenId, groupId, groupMemberId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteGroupMemberResponse(int serverPort, UUID accessTokenId, UUID groupId, UUID groupMemberId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, Endpoints.COMMUNITY_GROUP_DELETE_MEMBER, CollectionUtils.toMap(new BiWrapper<>("groupId", groupId), new BiWrapper<>("groupMemberId", groupMemberId))));
    }
}
