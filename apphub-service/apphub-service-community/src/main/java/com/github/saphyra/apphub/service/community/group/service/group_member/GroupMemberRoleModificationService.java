package com.github.saphyra.apphub.service.community.group.service.group_member;

import com.github.saphyra.apphub.api.community.model.response.group.GroupMemberResponse;
import com.github.saphyra.apphub.api.community.model.response.group.GroupMemberRoleRequest;
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
public class GroupMemberRoleModificationService {
    private final GroupMemberRoleRequestValidator groupMemberRoleRequestValidator;
    private final GroupDao groupDao;
    private final GroupMemberDao groupMemberDao;
    private final GroupMemberToResponseConverter groupMemberToResponseConverter;

    public GroupMemberResponse modifyRoles(UUID userId, UUID groupId, UUID groupMemberId, GroupMemberRoleRequest request) {
        groupMemberRoleRequestValidator.validate(request);

        GroupMember ownMember = groupMemberDao.findByGroupIdAndUserIdValidated(groupId, userId);

        if (!ownMember.isCanModifyRoles()) {
            throw ExceptionFactory.forbiddenOperation(userId + " must not modify roles in group " + groupId);
        }

        GroupMember memberToModify = groupMemberDao.findByIdValidated(groupMemberId);
        Group group = groupDao.findByIdValidated(groupId);
        if (group.getOwnerId().equals(memberToModify.getUserId())) {
            throw ExceptionFactory.forbiddenOperation(groupMemberId + " is the owner of group " + groupId);
        }

        memberToModify.setCanInvite(request.getCanInvite());
        memberToModify.setCanKick(request.getCanKick());
        memberToModify.setCanModifyRoles(request.getCanModifyRoles());

        groupMemberDao.save(memberToModify);

        return groupMemberToResponseConverter.convert(memberToModify);
    }
}
