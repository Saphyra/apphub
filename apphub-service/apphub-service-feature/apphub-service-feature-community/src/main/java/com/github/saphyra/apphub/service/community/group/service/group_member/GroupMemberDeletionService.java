package com.github.saphyra.apphub.service.community.group.service.group_member;

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
public class GroupMemberDeletionService {
    private final GroupDao groupDao;
    private final GroupMemberDao groupMemberDao;

    public void delete(UUID userId, UUID groupId, UUID groupMemberId) {
        GroupMember ownMember = groupMemberDao.findByGroupIdAndUserIdValidated(groupId, userId);
        GroupMember memberToDelete = groupMemberDao.findByIdValidated(groupMemberId);

        if (!ownMember.isCanKick() && !userId.equals(memberToDelete.getUserId())) {
            throw ExceptionFactory.forbiddenOperation(userId + " must not kick GroupMember" + groupMemberId + " from Group " + groupId);
        }

        Group group = groupDao.findByIdValidated(groupId);
        if (memberToDelete.getUserId().equals(group.getOwnerId())) {
            throw ExceptionFactory.forbiddenOperation("GroupMember " + groupMemberId + " is the owner of group " + groupId);
        }

        groupMemberDao.delete(memberToDelete);
    }
}
