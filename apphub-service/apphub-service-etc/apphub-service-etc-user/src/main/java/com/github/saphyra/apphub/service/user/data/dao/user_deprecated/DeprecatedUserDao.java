package com.github.saphyra.apphub.service.user.data.dao.user_deprecated;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Deprecated(forRemoval = true)
public class DeprecatedUserDao extends AbstractDao<UserEntity, DeprecatedUser, String, DeprecatedUserRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;
    private final int maxNumberOfUsersFound;

    @Builder
    DeprecatedUserDao(
        UserConverter converter,
        DeprecatedUserRepository repository,
        UuidConverter uuidConverter,
        @Value("${maxNumberOfUsersFound}") int maxNumberOfUsersFound
    ) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
        this.maxNumberOfUsersFound = maxNumberOfUsersFound;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        deleteById(uuidConverter.convertDomain(userId));
    }

    public Optional<DeprecatedUser> findByEmail(String email) {
        return converter.convertEntity(repository.findByEmail(email));
    }

    public Optional<DeprecatedUser> findByUsername(String username) {
        return converter.convertEntity(repository.findByUsername(username));
    }

    public DeprecatedUser findByIdValidated(UUID userId) {
        return findById(uuidConverter.convertDomain(userId))
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND, "User not found with id " + userId));
    }

    public List<DeprecatedUser> getByUsernameOrEmailContainingIgnoreCase(String queryString) {
        return converter.convertEntity(repository.getByUsernameOrEmailContainingIgnoreCase(queryString, PageRequest.of(0, maxNumberOfUsersFound)));
    }

    public List<DeprecatedUser> getUsersMarkedToDelete() {
        return converter.convertEntity(repository.getByUsersMarkedToDelete());
    }

    public Optional<DeprecatedUser> findById(UUID userId) {
        return findById(uuidConverter.convertDomain(userId));
    }

    public Optional<DeprecatedUser> findByUsernameOrEmail(String input) {
        return converter.convertEntity(repository.findByUsernameOrEmail(input));
    }
}
