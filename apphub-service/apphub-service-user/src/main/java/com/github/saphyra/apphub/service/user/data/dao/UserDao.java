package com.github.saphyra.apphub.service.user.data.dao;

import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import com.github.saphyra.apphub.lib.error_handler.exception.NotFoundException;
import com.github.saphyra.dao.AbstractDao;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserDao extends AbstractDao<UserEntity, User, String, UserRepository> {
    private final UuidConverter uuidConverter;

    public UserDao(UserConverter converter, UserRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public Optional<User> findByEmail(String email) {
        return converter.convertEntity(repository.findByEmail(email));
    }

    public Optional<User> findByUsername(String username) {
        return converter.convertEntity(repository.findByUsername(username));
    }

    //TODO unit test
    public User findById(UUID userId) {
        return findById(uuidConverter.convertDomain(userId))
            .orElseThrow(() -> new NotFoundException(new ErrorMessage(ErrorCode.USER_NOT_FOUND.name()), String.format("User not found with userId %s", userId)));
    }

    //TODO unit test
    public void deleteById(UUID userId) {
        deleteById(uuidConverter.convertDomain(userId));
    }
}
