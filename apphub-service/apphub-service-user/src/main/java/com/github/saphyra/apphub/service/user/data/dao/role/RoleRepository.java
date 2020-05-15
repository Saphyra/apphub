package com.github.saphyra.apphub.service.user.data.dao.role;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

//TODO unit test
public interface RoleRepository extends CrudRepository<RoleEntity, String> {
    List<RoleEntity> getByUserId(String userId);
}
