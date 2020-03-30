package com.github.saphyra.apphub.service.user.authentication.dao;

import org.springframework.data.repository.CrudRepository;

public interface AccessTokenRepository extends CrudRepository<AccessTokenEntity, String> {
}
