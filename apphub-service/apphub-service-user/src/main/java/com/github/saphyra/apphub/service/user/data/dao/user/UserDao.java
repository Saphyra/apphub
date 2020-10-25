package com.github.saphyra.apphub.service.user.data.dao.user;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
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

    public User findById(UUID userId) {
        return findById(uuidConverter.convertDomain(userId))
            .orElseThrow(() -> new NotFoundException(new ErrorMessage(ErrorCode.USER_NOT_FOUND.name()), String.format("User not found with userId %s", userId)));
    }

    public List<User> getByUsernameOrEmailContainingIgnoreCase(String queryString) {
        return converter.convertEntity(repository.getByUsernameOrEmailContainingIgnoreCase(queryString));
    }
}
