package com.github.saphyra.apphub.service.user.authentication.dao;

import com.github.saphyra.dao.AbstractDao;
import org.springframework.stereotype.Component;

@Component
public class AccessTokenDao extends AbstractDao<AccessTokenEntity, AccessToken, String, AccessTokenRepository> {
    public AccessTokenDao(AccessTokenConverter converter, AccessTokenRepository repository) {
        super(converter, repository);
    }
}
