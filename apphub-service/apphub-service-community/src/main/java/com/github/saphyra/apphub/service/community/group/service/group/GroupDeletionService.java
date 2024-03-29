package com.github.saphyra.apphub.service.community.group.service.group;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.community.group.dao.group.Group;
import com.github.saphyra.apphub.service.community.group.dao.group.GroupDao;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMemberDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroupDeletionService {
    private final GroupDao groupDao;
    private final GroupMemberDao groupMemberDao;

    @Transactional
    public void deleteGroup(UUID userId, UUID groupId) {
        Group group = groupDao.findByIdValidated(groupId);

        if (!group.getOwnerId().equals(userId)) {
            throw ExceptionFactory.forbiddenOperation(userId + " must not delete group " + groupId);
        }

        groupMemberDao.deleteByGroupId(groupId);
        groupDao.delete(group);
    }
}
