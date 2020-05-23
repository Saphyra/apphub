package com.github.saphyra.apphub.service.user.data.dao.role;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RoleRepository extends CrudRepository<RoleEntity, String> {
    List<RoleEntity> getByUserId(String userId);
}
