package com.github.saphyra.apphub.service.user.data.dao;

import com.github.saphyra.dao.AbstractDao;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
//TODO unit test
public class UserDao extends AbstractDao<UserEntity, User, String, UserRepository> {
    public UserDao(UserConverter converter, UserRepository repository) {
        super(converter, repository);
    }

    public Optional<User> findByEmail(String email) {
        return converter.convertEntity(repository.findByEmail(email));
    }

    public Optional<User> findByUsername(String username) {
        return converter.convertEntity(repository.findByUsername(username));
    }
}
