package com.github.saphyra.apphub.service.user.data.dao.role;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RoleDao extends AbstractDao<RoleEntity, Role, String, RoleRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public RoleDao(RoleConverter converter, RoleRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public Optional<Role> findByUserIdAndRole(UUID userId, String role) {
        return converter.convertEntity(repository.findByUserIdAndRole(uuidConverter.convertDomain(userId), role));
    }

    public List<Role> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public void deleteByRole(String role) {
        repository.deleteByRole(role);
    }
}
