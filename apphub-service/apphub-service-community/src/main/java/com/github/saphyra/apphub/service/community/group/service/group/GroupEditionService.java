package com.github.saphyra.apphub.service.community.group.service.group;

import com.github.saphyra.apphub.api.community.model.response.group.GroupInvitationType;
import com.github.saphyra.apphub.api.community.model.response.group.GroupListResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.community.group.dao.group.Group;
import com.github.saphyra.apphub.service.community.group.dao.group.GroupDao;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMember;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMemberDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class GroupEditionService {
    private final GroupNameValidator groupNameValidator;
    private final GroupDao groupDao;
    private final GroupToResponseConverter groupToResponseConverter;
    private final GroupMemberDao groupMemberDao;

    public GroupListResponse rename(UUID userId, UUID groupId, String groupName) {
        groupNameValidator.validate(groupName);

        Group group = groupDao.findByIdValidated(groupId);

        if (!group.getOwnerId().equals(userId)) {
            throw ExceptionFactory.forbiddenOperation(userId + " must not rename group " + groupId);
        }

        group.setName(groupName);
        groupDao.save(group);

        return groupToResponseConverter.convert(group);
    }

    public GroupListResponse changeInvitationType(UUID userId, UUID groupId, GroupInvitationType invitationType) {
        ValidationUtil.notNull(invitationType, "invitationType");

        Group group = groupDao.findByIdValidated(groupId);

        if (!group.getOwnerId().equals(userId)) {
            throw ExceptionFactory.forbiddenOperation(userId + " must not rename group " + groupId);
        }

        group.setInvitationType(invitationType);
        groupDao.save(group);

        return groupToResponseConverter.convert(group);
    }

    @Transactional
    public void changeOwner(UUID userId, UUID groupId, UUID groupMemberId) {
        ValidationUtil.notNull(groupMemberId, "groupMemberId");

        Group group = groupDao.findByIdValidated(groupId);
        if (!userId.equals(group.getOwnerId())) {
            throw ExceptionFactory.forbiddenOperation(userId + " must not change owner of group {}" + groupId);
        }

        GroupMember member = groupMemberDao.findByIdValidated(groupMemberId);
        if (member.getUserId().equals(userId)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.GENERAL_ERROR, groupMemberId + " is already the owner of group " + groupId);
        }

        group.setOwnerId(member.getUserId());
        groupDao.save(group);

        member.setCanKick(true);
        member.setCanInvite(true);
        member.setCanModifyRoles(true);
        groupMemberDao.save(member);
    }
}
