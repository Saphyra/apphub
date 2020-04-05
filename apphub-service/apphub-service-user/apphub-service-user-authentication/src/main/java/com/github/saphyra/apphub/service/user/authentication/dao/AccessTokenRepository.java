package com.github.saphyra.apphub.service.user.authentication.dao;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;

interface AccessTokenRepository extends CrudRepository<AccessTokenEntity, String> {
    @Transactional
    void deleteByPersistentAndLastAccessBefore(boolean persistent, OffsetDateTime expiration);
}
