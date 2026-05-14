package com.github.saphyra.apphub.service.user.disabled_role.dao;

import com.github.saphyra.apphub.lib.common_domain.Role;
import org.springframework.data.repository.CrudRepository;

public interface DisabledRoleRepository extends CrudRepository<DisabledRoleEntity, Role> {
}
