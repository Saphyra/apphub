package com.github.saphyra.apphub.service.user.data.dao.role;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

interface RoleRepository extends CrudRepository<RoleEntity, String> {
    Optional<RoleEntity> findByUserIdAndRole(String userId, String role);

    List<RoleEntity> getByUserId(String userId);
}
