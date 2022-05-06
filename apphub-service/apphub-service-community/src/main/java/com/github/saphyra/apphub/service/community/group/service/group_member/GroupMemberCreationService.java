package com.github.saphyra.apphub.service.community.group.service.group_member;

import com.github.saphyra.apphub.api.community.model.response.group.GroupMemberResponse;
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

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroupMemberCreationService {
    private final GroupDao groupDao;
    private final GroupMemberDao groupMemberDao;
    private final GroupMemberCandidateCollector groupMemberCandidateCollector;
    private final GroupMemberFactory groupMemberFactory;
    private final GroupMemberToResponseConverter groupMemberToResponseConverter;

    public GroupMemberResponse create(UUID userId, UUID groupId, UUID memberUserId) {
        ValidationUtil.notNull(memberUserId, "memberUserId");

        GroupMember invitorMember = groupMemberDao.findByGroupIdAndUserIdValidated(groupId, userId);

        if (!invitorMember.isCanInvite()) {
            throw ExceptionFactory.forbiddenOperation(userId + " must not invite members to groupId " + groupId);
        }

        Group group = groupDao.findByIdValidated(groupId);
        if (!groupMemberCandidateCollector.getCandidateUserIds(group).contains(memberUserId)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS, memberUserId + " must not be invited to group " + groupId);
        }

        GroupMember groupMember = groupMemberFactory.create(groupId, memberUserId, false);
        groupMemberDao.save(groupMember);
        return groupMemberToResponseConverter.convert(groupMember);
    }
}
