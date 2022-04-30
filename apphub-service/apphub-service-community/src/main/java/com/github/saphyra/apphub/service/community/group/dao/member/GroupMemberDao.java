package com.github.saphyra.apphub.service.community.group.dao.member;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GroupMemberDao extends AbstractDao<GroupMemberEntity, GroupMember, String, GroupMemberRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public GroupMemberDao(GroupMemberConverter converter, GroupMemberRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public void deleteByGroupId(UUID groupId) {
        repository.deleteByGroupId(uuidConverter.convertDomain(groupId));
    }
}
