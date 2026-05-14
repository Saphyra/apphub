package com.github.saphyra.apphub.service.user.data.dao.role_deprecated;

import org.springframework.data.repository.CrudRepository;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Deprecated(forRemoval = true)
interface RoleRepository extends CrudRepository<RoleEntity, String> {
    Optional<RoleEntity> findByUserIdAndRole(String userId, String role);

    List<RoleEntity> getByUserId(String userId);

    void deleteByUserId(String userId);

    @Transactional
    void deleteByRole(String role);
}
