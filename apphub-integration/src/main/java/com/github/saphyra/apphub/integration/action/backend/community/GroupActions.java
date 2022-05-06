package com.github.saphyra.apphub.integration.action.backend.community;

import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.community.GroupInvitationType;
import com.github.saphyra.apphub.integration.structure.community.GroupListResponse;
import com.github.saphyra.apphub.integration.structure.community.GroupMemberResponse;
import com.github.saphyra.apphub.integration.structure.community.GroupMemberRoleRequest;
import com.github.saphyra.apphub.integration.structure.community.SearchResultItem;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupActions {
    public static GroupListResponse createGroup(Language language, UUID accessTokenId, String groupName) {
        Response response = getCreateGroupResponse(language, accessTokenId, groupName);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(GroupListResponse.class);
    }

    public static Response getCreateGroupResponse(Language language, UUID accessTokenId, String groupName) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(groupName))
            .put(UrlFactory.create(Endpoints.COMMUNITY_GROUP_CREATE));
    }

    public static GroupListResponse renameGroup(Language language, UUID accessTokenId, UUID groupId, String groupName) {
        Response response = getRenameGroupResponse(language, accessTokenId, groupId, groupName);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(GroupListResponse.class);
    }

    public static Response getRenameGroupResponse(Language language, UUID accessTokenId, UUID groupId, String groupName) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(groupName))
            .post(UrlFactory.create(Endpoints.COMMUNITY_GROUP_RENAME, "groupId", groupId));
    }

    public static GroupMemberResponse createMember(Language language, UUID accessTokenId, UUID groupId, UUID userId) {
        Response response = getCreateGroupMemberResponse(language, accessTokenId, groupId, userId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(GroupMemberResponse.class);
    }

    public static Response getCreateGroupMemberResponse(Language language, UUID accessTokenId, UUID groupId, UUID userId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(userId))
            .put(UrlFactory.create(Endpoints.COMMUNITY_GROUP_CREATE_MEMBER, "groupId", groupId));
    }

    public static GroupListResponse changeInvitationType(Language language, UUID accessTokenId, UUID groupId, GroupInvitationType invitationType) {
        Response response = getChangeInvitationTypeResponse(language, accessTokenId, groupId, invitationType);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(GroupListResponse.class);
    }

    public static Response getChangeInvitationTypeResponse(Language language, UUID accessTokenId, UUID groupId, GroupInvitationType invitationType) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(invitationType))
            .post(UrlFactory.create(Endpoints.COMMUNITY_GROUP_CHANGE_INVITATION_TYPE, "groupId", groupId));
    }

    public static void changeOwner(Language language, UUID accessTokenId, UUID groupId, UUID groupMemberId) {
        Response response = getChangeOwnerResponse(language, accessTokenId, groupId, groupMemberId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getChangeOwnerResponse(Language language, UUID accessTokenId, UUID groupId, UUID groupMemberId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(groupMemberId))
            .post(UrlFactory.create(Endpoints.COMMUNITY_GROUP_CHANGE_OWNER, "groupId", groupId));
    }

    public static List<GroupMemberResponse> getMembers(Language language, UUID accessTokenId, UUID groupId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.COMMUNITY_GROUP_GET_MEMBERS, "groupId", groupId));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(GroupMemberResponse[].class));
    }

    public static List<GroupListResponse> getGroups(Language language, UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.COMMUNITY_GET_GROUPS));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(GroupListResponse[].class));
    }

    public static void deleteGroup(Language language, UUID accessTokenId, UUID groupId) {
        Response response = getDeleteGroupResponse(language, accessTokenId, groupId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteGroupResponse(Language language, UUID accessTokenId, UUID groupId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .delete(UrlFactory.create(Endpoints.COMMUNITY_GROUP_DELETE, "groupId", groupId));
    }

    public static List<SearchResultItem> search(Language language, UUID accessTokenId, UUID groupId, String query) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(query))
            .post(UrlFactory.create(Endpoints.COMMUNITY_GROUP_SEARCH_MEMBER_CANDIDATES, "groupId", groupId));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(SearchResultItem[].class));
    }

    public static GroupMemberResponse modifyRoles(Language language, UUID accessTokenId, UUID groupId, UUID groupMemberId, GroupMemberRoleRequest request) {
        Response response = getModifyRolesResponse(language, accessTokenId, groupId, groupMemberId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(GroupMemberResponse.class);
    }

    public static Response getModifyRolesResponse(Language language, UUID accessTokenId, UUID groupId, UUID groupMemberId, GroupMemberRoleRequest request) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(request)
            .post(UrlFactory.create(Endpoints.COMMUNITY_GROUP_MEMBER_ROLES, CollectionUtils.toMap(new BiWrapper<>("groupId", groupId), new BiWrapper<>("groupMemberId", groupMemberId))));
    }

    public static void deleteGroupMember(Language language, UUID accessTokenId, UUID groupId, UUID groupMemberId) {
        Response response = getDeleteGroupMemberResponse(language, accessTokenId, groupId, groupMemberId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteGroupMemberResponse(Language language, UUID accessTokenId, UUID groupId, UUID groupMemberId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .delete(UrlFactory.create(Endpoints.COMMUNITY_GROUP_DELETE_MEMBER, CollectionUtils.toMap(new BiWrapper<>("groupId", groupId), new BiWrapper<>("groupMemberId", groupMemberId))));
    }
}
