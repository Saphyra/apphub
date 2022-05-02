package com.github.saphyra.apphub.service.community.group.dao.group;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class GroupDao extends AbstractDao<GroupEntity, Group, String, GroupRepository> {
    private final UuidConverter uuidConverter;

    public GroupDao(GroupConverter converter, GroupRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public List<Group> getByOwnerId(UUID ownerId) {
        return converter.convertEntity(repository.getByOwnerId(uuidConverter.convertDomain(ownerId)));
    }

    //TODO unit test
    public Optional<Group> findById(UUID groupId) {
        return findById(uuidConverter.convertDomain(groupId));
    }

    //TODO unit test
    public Group findByIdValidated(UUID groupId) {
        return findById(groupId)
            .orElseThrow(() -> ExceptionFactory.reportedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Group not found with id " + groupId));
    }
}
