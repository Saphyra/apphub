package com.github.saphyra.apphub.service.community.group.dao.group;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
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
}
