package com.github.saphyra.apphub.service.user.data.dao.role;

import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.dao.AbstractDao;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RoleDao extends AbstractDao<RoleEntity, Role, String, RoleRepository> {
    private final UuidConverter uuidConverter;

    public RoleDao(RoleConverter converter, RoleRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public List<Role> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }
}
