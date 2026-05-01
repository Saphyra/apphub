package com.github.saphyra.apphub.service.community.group;

import com.github.saphyra.apphub.api.feature.community.model.response.SearchResultItem;
import com.github.saphyra.apphub.api.feature.community.model.response.group.GroupInvitationType;
import com.github.saphyra.apphub.api.feature.community.model.response.group.GroupListResponse;
import com.github.saphyra.apphub.api.feature.community.model.response.group.GroupMemberResponse;
import com.github.saphyra.apphub.api.feature.community.model.response.group.GroupMemberRoleRequest;
import com.github.saphyra.apphub.api.feature.community.server.GroupController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.community.group.service.group.GroupCreationService;
import com.github.saphyra.apphub.service.community.group.service.group.GroupDeletionService;
import com.github.saphyra.apphub.service.community.group.service.group_member.GroupMemberCandidateQueryService;
import com.github.saphyra.apphub.service.community.group.service.group_member.GroupMemberCreationService;
import com.github.saphyra.apphub.service.community.group.service.group_member.GroupMemberDeletionService;
import com.github.saphyra.apphub.service.community.group.service.group_member.GroupMemberQueryService;
import com.github.saphyra.apphub.service.community.group.service.group_member.GroupMemberRoleModificationService;
import com.github.saphyra.apphub.service.community.group.service.group.GroupQueryService;
import com.github.saphyra.apphub.service.community.group.service.group.GroupEditionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GroupControllerImpl implements GroupController {
    private final GroupQueryService groupQueryService;
    private final GroupCreationService groupCreationService;
    private final GroupEditionService groupEditionService;
    private final GroupMemberQueryService groupMemberQueryService;
    private final GroupMemberCandidateQueryService groupMemberCandidateQueryService;
    private final GroupMemberCreationService groupMemberCreationService;
    private final GroupMemberDeletionService groupMemberDeletionService;
    private final GroupMemberRoleModificationService groupMemberRoleModificationService;
    private final GroupDeletionService groupDeletionService;

    @Override
    public List<GroupListResponse> getGroups(AccessToken accessToken) {
        log.info("{} wants to know his groups", accessToken.getUserId());
        return groupQueryService.getGroups(accessToken.getUserId());
    }

    @Override
    public GroupListResponse createGroup(OneParamRequest<String> groupName, AccessToken accessToken) {
        log.info("{} wants to create group with name {}", accessToken.getUserId(), groupName);
        return groupCreationService.create(accessToken.getUserId(), groupName.getValue());
    }

    @Override
    public void deleteGroup(UUID groupId, AccessToken accessToken) {
        log.info("{} wants to delete group {}", accessToken.getUserId(), groupId);
        groupDeletionService.deleteGroup(accessToken.getUserId(), groupId);
    }

    @Override
    public void changeOwner(OneParamRequest<UUID> groupMemberId, UUID groupId, AccessToken accessToken) {
        log.info("{} wants to change owner of group {} to {}", accessToken.getUserId(), groupId, groupMemberId);
        groupEditionService.changeOwner(accessToken.getUserId(), groupId, groupMemberId.getValue());
    }

    @Override
    public GroupListResponse renameGroup(OneParamRequest<String> groupName, UUID groupId, AccessToken accessToken) {
        log.info("{} wants to rename group {} to {}", accessToken.getUserId(), groupId, groupName);
        return groupEditionService.rename(accessToken.getUserId(), groupId, groupName.getValue());
    }

    @Override
    public GroupListResponse changeInvitationType(OneParamRequest<GroupInvitationType> invitationType, UUID groupId, AccessToken accessToken) {
        log.info("{} wants to change invitationType of Group {} to {}", accessToken.getUserId(), groupId, invitationType.getValue());
        return groupEditionService.changeInvitationType(accessToken.getUserId(), groupId, invitationType.getValue());
    }

    @Override
    public List<GroupMemberResponse> getMembersOfGroup(UUID groupId, AccessToken accessToken) {
        log.info("{} wants to know members of group {}", accessToken.getUserId(), groupId);
        return groupMemberQueryService.getMembers(accessToken.getUserId(), groupId);
    }

    @Override
    public List<SearchResultItem> searchMemberCandidates(OneParamRequest<String> queryString, UUID groupId, AccessToken accessToken) {
        log.info("{} wants to search GroupMember candidates for Group {}based on text {}", accessToken.getUserId(), groupId, queryString.getValue());
        return groupMemberCandidateQueryService.search(accessToken.getUserId(), groupId, queryString.getValue());
    }

    @Override
    public GroupMemberResponse createMember(OneParamRequest<UUID> memberUserId, UUID groupId, AccessToken accessToken) {
        log.info("{} wants to add user {} to Group {}", accessToken.getUserId(), memberUserId, groupId);
        return groupMemberCreationService.create(accessToken.getUserId(), groupId, memberUserId.getValue());
    }

    @Override
    public void deleteMember(UUID groupId, UUID groupMemberId, AccessToken accessToken) {
        log.info("{} wants to delete GroupMember {} of Group {}", accessToken.getUserId(), groupMemberId, groupId);
        groupMemberDeletionService.delete(accessToken.getUserId(), groupId, groupMemberId);
    }

    @Override
    public GroupMemberResponse modifyRoles(GroupMemberRoleRequest request, UUID groupId, UUID groupMemberId, AccessToken accessToken) {
        log.info("{} wants to modify roles of {} to {} in group {}", accessToken.getUserId(), groupId, request, groupId);
        return groupMemberRoleModificationService.modifyRoles(accessToken.getUserId(), groupId, groupMemberId, request);
    }
}
