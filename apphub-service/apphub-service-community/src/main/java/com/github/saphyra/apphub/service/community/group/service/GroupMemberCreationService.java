package com.github.saphyra.apphub.service.community.group.service;

import com.github.saphyra.apphub.api.community.model.response.group.GroupMemberResponse;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.community.group.dao.group.Group;
import com.github.saphyra.apphub.service.community.group.dao.group.GroupDao;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMember;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMemberDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class GroupMemberCreationService {
    private final GroupDao groupDao;
    private final GroupMemberDao groupMemberDao;
    private final GroupMemberCandidateCollector groupMemberCandidateCollector;
    private final GroupMemberFactory groupMemberFactory;
    private final GroupMemberToResponseConverter groupMemberToResponseConverter;

    public GroupMemberResponse create(UUID userId, UUID groupId, UUID memberUserId) {
        Group group = groupDao.findByIdValidated(groupId);
        GroupMember invitorMember = groupMemberDao.findByGroupIdAndUserIdValidated(groupId, userId);

        if (!group.getOwnerId().equals(userId) && !invitorMember.isCanInvite()) {
            throw ExceptionFactory.forbiddenOperation(userId + " must not invite members to groupId " + groupId);
        }

        if (!groupMemberCandidateCollector.getCandidateUserIds(group).contains(memberUserId)) {
            throw ExceptionFactory.forbiddenOperation(memberUserId + " must not be invited to group " + groupId);
        }

        GroupMember groupMember = groupMemberFactory.create(groupId, memberUserId, false);
        groupMemberDao.save(groupMember);
        return groupMemberToResponseConverter.convert(groupMember);
    }
}
