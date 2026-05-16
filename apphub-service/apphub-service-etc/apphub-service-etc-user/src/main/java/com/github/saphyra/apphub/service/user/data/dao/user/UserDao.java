package com.github.saphyra.apphub.service.user.data.dao.user;

import com.github.saphyra.apphub.lib.common_domain.Role;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDao implements DeleteByUserIdDao {
    private final UserRepository repository;
    private final UuidConverter uuidConverter;
    private final DateTimeUtil dateTimeUtil;

    public void saveNew(User user) {
        String username = user.getUsername().toLowerCase();
        try {
            repository.trySaveCredential(uuidConverter.convertDomain(user.getUserId()), username);
        } catch (ConditionalCheckFailedException e) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.USERNAME_ALREADY_EXISTS, "Username already exists: " + username);
        }

        try {
            String email = user.getEmail();
            if (!username.equals(email)) {
                repository.trySaveCredential(uuidConverter.convertDomain(user.getUserId()), email);
            } else {
                log.info("Username equals the user's e-mail. Skipping credential creation.");
            }
        } catch (ConditionalCheckFailedException e) {
            repository.deleteCredential(username.toLowerCase());
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.EMAIL_ALREADY_EXISTS, "Email already exists: " + user.getEmail());
        }

        saveProfile(user);
        user.getRoles()
            .forEach(role -> addRole(user.getUserId(), role));
    }

    /**
     * @param userIdentifier username (ignore case) or e-mail
     * @return the single user matching with the provided userIdentifier
     */
    public Optional<User> findByUserIdentifier(String userIdentifier) {
        String lower = userIdentifier.toLowerCase();

        return repository.findByCredential(lower)
            .map(CredentialEntity::getUserId)
            .map(uuidConverter::convertEntity)
            .flatMap(this::findByUserId);
    }

    public User findByUserIdValidated(UUID userId) {
        return findByUserId(userId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND, "User not found with id " + userId));
    }

    public Optional<User> findByUserId(UUID userId) {
        return repository.findByUserId(uuidConverter.convertDomain(userId))
            .map(user -> {
                ProfileEntity profile = user.getEntity1();
                List<Role> roles = user.getEntity2()
                    .stream()
                    .map(Role::valueOf)
                    .toList();

                return User.builder()
                    .userId(uuidConverter.convertEntity(profile.getUserId()))
                    .email(profile.getEmail())
                    .username(profile.getUsername())
                    .password(profile.getPassword())
                    .language(profile.getLanguage())
                    .markedForDeletionAt(user.getEntity3().map(dateTimeUtil::fromEpochSecond).orElse(null))
                    .lockedUntil(dateTimeUtil.fromEpochSecond(profile.getLockedUntil()))
                    .passwordFailureCount(profile.getPasswordFailureCount())
                    .roles(new ArrayList<>(roles))
                    .build();
            });
    }

    public void addRole(UUID userId, Role role) {
        repository.addRole(uuidConverter.convertDomain(userId), role.name());
    }

    public void removeRole(UUID userId, Role role) {
        repository.deleteRole(uuidConverter.convertDomain(userId), role.name());
    }

    /**
     * Saves a new or overwrites the existing {@link ProfileEntity} record for the user.
     * <p>
     * Does not handle modifications in other records by itself.
     */
    public void saveProfile(User user) {
        ProfileEntity profile = ProfileEntity.builder()
            .userId(uuidConverter.convertDomain(user.getUserId()))
            .email(user.getEmail())
            .username(user.getUsername())
            .password(user.getPassword())
            .language(user.getLanguage())
            .lockedUntil(dateTimeUtil.toEpochSecond(user.getLockedUntil()))
            .passwordFailureCount(user.getPasswordFailureCount())
            .build();

        repository.save(profile);
    }

    public void addRoleToAll(String role) {
        repository.getAllUserIds()
            .forEach(userId -> repository.addRole(userId, role));
    }

    public void deleteRoleFromAll(String role) {
        repository.getAllUserIds()
            .forEach(userId -> repository.deleteRole(userId, role));
    }

    public void updateMarkedForDeletion(User user) {
        if (user.isMarkedForDeletion()) {
            repository.markForDeletion(uuidConverter.convertDomain(user.getUserId()), dateTimeUtil.toEpochSecond(user.getMarkedForDeletionAt()));
        } else {
            repository.unmarkForDeletion(uuidConverter.convertDomain(user.getUserId()));
        }
    }

    public void changeUsername(String originalUsername, User user) {
        String newUsername = user.getUsername().toLowerCase();
        try {
            repository.trySaveCredential(uuidConverter.convertDomain(user.getUserId()), newUsername);
        } catch (ConditionalCheckFailedException e) {
            Optional<CredentialEntity> existingCredential = repository.findByCredential(newUsername);
            if (existingCredential.isPresent() && existingCredential.get().getUserId().equals(uuidConverter.convertDomain(user.getUserId()))) {
                log.info("Existing credential belongs to the same user. Skipping creation.");
            } else {
                throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.USERNAME_ALREADY_EXISTS, "Username already exists.");
            }
        }
        saveProfile(user);

        String originalLower = originalUsername.toLowerCase();
        if (!originalLower.equals(user.getEmail())) {
            repository.deleteCredential(originalLower);
        } else {
            log.info("Old username equals the user's e-mail. Skipping credential deletion.");
        }
    }

    public void changeEmail(String originalEmail, User user) {
        try {
            repository.trySaveCredential(uuidConverter.convertDomain(user.getUserId()), user.getEmail());
        } catch (ConditionalCheckFailedException e) {
            Optional<CredentialEntity> existingCredential = repository.findByCredential(user.getEmail());
            if (existingCredential.isPresent() && existingCredential.get().getUserId().equals(uuidConverter.convertDomain(user.getUserId()))) {
                log.info("Existing credential belongs to the same user. Skipping creation.");
            } else {
                throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.EMAIL_ALREADY_EXISTS, "Email already exists.");
            }
        }

        saveProfile(user);

        if (!originalEmail.equals(user.getUsername())) {
            repository.deleteCredential(originalEmail);
        } else {
            log.info("Old email equals the user's username. Skipping credential deletion.");
        }
    }

    /**
     * @return list of userIds where the account is marked for deletion before current time.
     */
    public List<UUID> getUsersMarkedForDeletion() {
        return uuidConverter.convertEntity(repository.getUserIdsMarkedForDeletion(dateTimeUtil.getCurrentTimeEpochSeconds()));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        String userIdString = uuidConverter.convertDomain(userId);
        repository.findByUserId(userIdString)
            .ifPresentOrElse(
                user -> {
                    ProfileEntity profile = user.getEntity1();

                    if (nonNull(profile)) {
                        repository.deleteCredential(profile.getUsername().toLowerCase());
                        repository.deleteCredential(profile.getEmail());
                    } else {
                        log.info("No profile found for userId {}. Skipping credential deletion.", userId);
                    }

                    user.getEntity2()
                        .forEach(role -> repository.deleteRole(userIdString, role));
                    repository.unmarkForDeletion(userIdString);
                    repository.deleteProfile(userIdString);
                },
                () -> log.info("User not found with id {}. Skipping deletion process.", userId)
            );
    }
}
