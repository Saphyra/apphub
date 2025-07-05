package com.github.saphyra.apphub.service.community.group.dao.member;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
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

    public List<GroupMember> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    public List<GroupMember> getByGroupId(UUID groupId) {
        return converter.convertEntity(repository.getByGroupId(uuidConverter.convertDomain(groupId)));
    }

    public GroupMember findByGroupIdAndUserIdValidated(UUID groupId, UUID userId) {
        return converter.convertEntity(repository.findByGroupIdAndUserId(uuidConverter.convertDomain(groupId), uuidConverter.convertDomain(userId)))
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "GroupMember not found for groupId " + groupId + " and userId " + userId));
    }

    public GroupMember findByIdValidated(UUID groupMemberId) {
        return findById(groupMemberId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "GroupMember not found for groupMemberId " + groupMemberId));
    }

    public Optional<GroupMember> findById(UUID groupMemberId) {
        return findById(uuidConverter.convertDomain(groupMemberId));
    }
}
