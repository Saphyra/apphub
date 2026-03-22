package com.github.saphyra.apphub.service.community.group.dao;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.service.community.group.dao.group.GroupDao;
import com.github.saphyra.apphub.service.community.group.dao.member.GroupMemberDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroupDeleteByUserIdDao implements DeleteByUserIdDao {
    private final GroupDao groupDao;
    private final GroupMemberDao groupMemberDao;

    @Override
    public void deleteByUserId(UUID userId) {
        groupDao.getByOwnerId(userId)
            .stream()
            .peek(group -> groupMemberDao.deleteByGroupId(group.getGroupId()))
            .forEach(groupDao::delete);
    }
}
