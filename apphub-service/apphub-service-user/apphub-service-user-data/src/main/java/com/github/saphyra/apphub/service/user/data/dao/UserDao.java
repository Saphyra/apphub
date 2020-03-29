package com.github.saphyra.apphub.service.user.data.dao;

import com.github.saphyra.dao.AbstractDao;
import org.springframework.stereotype.Component;

@Component
public class UserDao extends AbstractDao<UserEntity, User, String, UserRepository> {
    public UserDao(UserConverter converter, UserRepository repository) {
        super(converter, repository);
    }
}
