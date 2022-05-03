package com.github.saphyra.apphub.api.community.server;

import com.github.saphyra.apphub.api.community.model.response.SearchResultItem;
import com.github.saphyra.apphub.api.community.model.response.group.GroupInvitationType;
import com.github.saphyra.apphub.api.community.model.response.group.GroupListResponse;
import com.github.saphyra.apphub.api.community.model.response.group.GroupMemberResponse;
import com.github.saphyra.apphub.api.community.model.response.group.GroupMemberRoleRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

public interface GroupController {
    //TODO API test
    @GetMapping(Endpoints.COMMUNITY_GET_GROUPS)
    List<GroupListResponse> getGroups(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    //TODO API test
    @PutMapping(Endpoints.COMMUNITY_GROUP_CREATE)
    GroupListResponse createGroup(@RequestBody OneParamRequest<String> groupName, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    //TODO API test
    @PostMapping(Endpoints.COMMUNITY_GROUP_RENAME)
    GroupListResponse renameGroup(@RequestBody OneParamRequest<String> groupName, @PathVariable("groupId") UUID groupId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    //TODO API test
    @PostMapping(Endpoints.COMMUNITY_GROUP_CHANGE_INVITATION_TYPE)
    GroupListResponse changeInvitationType(@RequestBody OneParamRequest<GroupInvitationType> invitationType, @PathVariable("groupId") UUID groupId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    //TODO API test
    @GetMapping(Endpoints.COMMUNITY_GROUP_GET_MEMBERS)
    List<GroupMemberResponse> getMembersOfGroup(@PathVariable("groupId") UUID groupId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    //TODO API test
    @PostMapping(Endpoints.COMMUNITY_GROUP_SEARCH_MEMBER_CANDIDATES)
    List<SearchResultItem> searchMemberCandidates(@RequestBody OneParamRequest<String> queryString, @PathVariable("groupId") UUID groupId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    //TODO API test
    @PutMapping(Endpoints.COMMUNITY_GROUP_CREATE_MEMBER)
    GroupMemberResponse createMember(@RequestBody OneParamRequest<UUID> memberUserId, @PathVariable("groupId") UUID groupId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    //TODO API test
    @DeleteMapping(Endpoints.COMMUNITY_GROUP_DELETE_MEMBER)
    void deleteMember(@PathVariable("groupId") UUID groupId, @PathVariable("groupMemberId") UUID groupMemberId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.COMMUNITY_GROUP_MEMBER_ROLES)
    GroupMemberResponse modifyRoles(@RequestBody GroupMemberRoleRequest request, @PathVariable("groupId") UUID groupId, @PathVariable("groupMemberId") UUID groupMemberId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
