package com.github.saphyra.apphub.service.community.group;

import com.github.saphyra.apphub.api.community.model.response.SearchResultItem;
import com.github.saphyra.apphub.api.community.model.response.group.GroupInvitationType;
import com.github.saphyra.apphub.api.community.model.response.group.GroupListResponse;
import com.github.saphyra.apphub.api.community.model.response.group.GroupMemberResponse;
import com.github.saphyra.apphub.api.community.server.GroupController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.community.group.service.GroupCreationService;
import com.github.saphyra.apphub.service.community.group.service.GroupMemberCandidateQueryService;
import com.github.saphyra.apphub.service.community.group.service.GroupMemberCreationService;
import com.github.saphyra.apphub.service.community.group.service.GroupMemberDeletionService;
import com.github.saphyra.apphub.service.community.group.service.GroupMemberQueryService;
import com.github.saphyra.apphub.service.community.group.service.GroupQueryService;
import com.github.saphyra.apphub.service.community.group.service.EditGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class GroupControllerImpl implements GroupController {
    private final GroupQueryService groupQueryService;
    private final GroupCreationService groupCreationService;
    private final EditGroupService editGroupService;
    private final GroupMemberQueryService groupMemberQueryService;
    private final GroupMemberCandidateQueryService groupMemberCandidateQueryService;
    private final GroupMemberCreationService groupMemberCreationService;
    private final GroupMemberDeletionService groupMemberDeletionService;

    @Override
    public List<GroupListResponse> getGroups(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know his groups", accessTokenHeader.getUserId());
        return groupQueryService.getGroups(accessTokenHeader.getUserId());
    }

    @Override
    public GroupListResponse createGroup(OneParamRequest<String> groupName, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create group with name {}", accessTokenHeader.getUserId(), groupName);
        return groupCreationService.create(accessTokenHeader.getUserId(), groupName.getValue());
    }

    @Override
    public GroupListResponse renameGroup(OneParamRequest<String> groupName, UUID groupId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to rename group {} to {}", accessTokenHeader.getUserId(), groupId, groupName);
        return editGroupService.rename(accessTokenHeader.getUserId(), groupId, groupName.getValue());
    }

    @Override
    public GroupListResponse changeInvitationType(OneParamRequest<GroupInvitationType> invitationType, UUID groupId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to change invitationType of Group {} to {}", accessTokenHeader.getUserId(), groupId, invitationType.getValue());
        return editGroupService.changeInvitationType(accessTokenHeader.getUserId(), groupId, invitationType.getValue());
    }

    @Override
    public List<GroupMemberResponse> getMembersOfGroup(UUID groupId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know members of group {}", accessTokenHeader.getUserId(), groupId);
        return groupMemberQueryService.getMembers(accessTokenHeader.getUserId(), groupId);
    }

    @Override
    public List<SearchResultItem> searchMemberCandidates(OneParamRequest<String> queryString, UUID groupId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to search GroupMember candidates for Group {}based on text {}", accessTokenHeader.getUserId(), groupId, queryString.getValue());
        return groupMemberCandidateQueryService.search(accessTokenHeader.getUserId(), groupId, queryString.getValue());
    }

    @Override
    public GroupMemberResponse createMember(OneParamRequest<UUID> memberUserId, UUID groupId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to add user {} to Group {}", accessTokenHeader.getUserId(), memberUserId, groupId);
        return groupMemberCreationService.create(accessTokenHeader.getUserId(), groupId, memberUserId.getValue());
    }

    @Override
    public void deleteMember(UUID groupId, UUID groupMemberId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete GroupMember {} of Group {}", accessTokenHeader.getUserId(), groupMemberId, groupId);
        groupMemberDeletionService.delete(accessTokenHeader.getUserId(), groupId, groupMemberId);
    }
}
