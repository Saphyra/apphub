package com.github.saphyra.apphub.integration.action.backend.community;

import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.CommunityEndpoints;
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
    public static GroupListResponse createGroup(int serverPort, String accessToken, String groupName) {
        Response response = getCreateGroupResponse(serverPort, accessToken, groupName);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(GroupListResponse.class);
    }

    public static Response getCreateGroupResponse(int serverPort, String accessToken, String groupName) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(groupName))
            .put(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_GROUP_CREATE));
    }

    public static GroupListResponse renameGroup(int serverPort, String accessToken, UUID groupId, String groupName) {
        Response response = getRenameGroupResponse(serverPort, accessToken, groupId, groupName);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(GroupListResponse.class);
    }

    public static Response getRenameGroupResponse(int serverPort, String accessToken, UUID groupId, String groupName) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(groupName))
            .post(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_GROUP_RENAME, "groupId", groupId));
    }

    public static GroupMemberResponse createMember(int serverPort, String accessToken, UUID groupId, UUID userId) {
        Response response = getCreateGroupMemberResponse(serverPort, accessToken, groupId, userId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(GroupMemberResponse.class);
    }

    public static Response getCreateGroupMemberResponse(int serverPort, String accessToken, UUID groupId, UUID userId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(userId))
            .put(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_GROUP_CREATE_MEMBER, "groupId", groupId));
    }

    public static GroupListResponse changeInvitationType(int serverPort, String accessToken, UUID groupId, GroupInvitationType invitationType) {
        Response response = getChangeInvitationTypeResponse(serverPort, accessToken, groupId, invitationType);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(GroupListResponse.class);
    }

    public static Response getChangeInvitationTypeResponse(int serverPort, String accessToken, UUID groupId, GroupInvitationType invitationType) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(invitationType))
            .post(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_GROUP_CHANGE_INVITATION_TYPE, "groupId", groupId));
    }

    public static void changeOwner(int serverPort, String accessToken, UUID groupId, UUID groupMemberId) {
        Response response = getChangeOwnerResponse(serverPort, accessToken, groupId, groupMemberId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getChangeOwnerResponse(int serverPort, String accessToken, UUID groupId, UUID groupMemberId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(groupMemberId))
            .post(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_GROUP_CHANGE_OWNER, "groupId", groupId));
    }

    public static List<GroupMemberResponse> getMembers(int serverPort, String accessToken, UUID groupId) {
        Response response = getMembersResponse(serverPort, accessToken, groupId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(GroupMemberResponse[].class));
    }

    public static Response getMembersResponse(int serverPort, String accessToken, UUID groupId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_GROUP_GET_MEMBERS, "groupId", groupId));
    }

    public static List<GroupListResponse> getGroups(int serverPort, String accessToken) {
        Response response = getGroupsResponse(serverPort, accessToken);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(GroupListResponse[].class));
    }

    public static Response getGroupsResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_GET_GROUPS));
    }

    public static void deleteGroup(int serverPort, String accessToken, UUID groupId) {
        Response response = getDeleteGroupResponse(serverPort, accessToken, groupId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteGroupResponse(int serverPort, String accessToken, UUID groupId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .delete(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_GROUP_DELETE, "groupId", groupId));
    }

    public static List<SearchResultItem> search(int serverPort, String accessToken, UUID groupId, String query) {
        Response response = getSearchResponse(serverPort, accessToken, groupId, query);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(SearchResultItem[].class));
    }

    public static Response getSearchResponse(int serverPort, String accessToken, UUID groupId, String query) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(query))
            .post(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_GROUP_SEARCH_MEMBER_CANDIDATES, "groupId", groupId));
    }

    public static GroupMemberResponse modifyRoles(int serverPort, String accessToken, UUID groupId, UUID groupMemberId, GroupMemberRoleRequest request) {
        Response response = getModifyRolesResponse(serverPort, accessToken, groupId, groupMemberId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(GroupMemberResponse.class);
    }

    public static Response getModifyRolesResponse(int serverPort, String accessToken, UUID groupId, UUID groupMemberId, GroupMemberRoleRequest request) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(request)
            .post(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_GROUP_MEMBER_ROLES, CollectionUtils.toMap(new BiWrapper<>("groupId", groupId), new BiWrapper<>("groupMemberId", groupMemberId))));
    }

    public static void deleteGroupMember(int serverPort, String accessToken, UUID groupId, UUID groupMemberId) {
        Response response = getDeleteGroupMemberResponse(serverPort, accessToken, groupId, groupMemberId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteGroupMemberResponse(int serverPort, String accessToken, UUID groupId, UUID groupMemberId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .delete(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_GROUP_DELETE_MEMBER, CollectionUtils.toMap(new BiWrapper<>("groupId", groupId), new BiWrapper<>("groupMemberId", groupMemberId))));
    }
}
