package com.github.saphyra.apphub.service.user.data.dao.user;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserDao extends AbstractDao<UserEntity, User, String, UserRepository> {
    private final UuidConverter uuidConverter;

    public UserDao(UserConverter converter, UserRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteById(UUID userId) {
        deleteById(uuidConverter.convertDomain(userId));
    }

    public Optional<User> findByEmail(String email) {
        return converter.convertEntity(repository.findByEmail(email));
    }

    public Optional<User> findByUsername(String username) {
        return converter.convertEntity(repository.findByUsername(username));
    }

    public User findByIdValidated(UUID userId) {
        return findById(uuidConverter.convertDomain(userId))
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND, "User not found with id " + userId));
    }

    public List<User> getByUsernameOrEmailContainingIgnoreCase(String queryString) {
        return converter.convertEntity(repository.getByUsernameOrEmailContainingIgnoreCase(queryString));
    }

    public List<User> getUsersMarkedToDelete() {
        return converter.convertEntity(repository.getByUsersMarkedToDelete());
    }

    //TODO unit test
    public Optional<User> findById(UUID userId) {
        return findById(uuidConverter.convertDomain(userId));
    }
}
