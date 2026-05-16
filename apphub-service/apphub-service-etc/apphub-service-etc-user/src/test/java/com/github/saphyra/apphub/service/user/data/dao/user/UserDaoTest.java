package com.github.saphyra.apphub.service.user.data.dao.user;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.Role;
import com.github.saphyra.apphub.lib.common_domain.TriWrapper;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UserDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String EMAIL = "test@email.com";
    private static final String USERNAME = "test-username";
    private static final String PASSWORD = "test-password";
    private static final String LANGUAGE = "hu";
    private static final LocalDateTime MARKED_FOR_DELETION_AT = LocalDateTime.now();
    private static final LocalDateTime LOCKED_UNTIL = LocalDateTime.now();
    private static final long LOCKED_UNTIL_EPOCH = LOCKED_UNTIL.toEpochSecond(ZoneOffset.UTC);
    private static final int PASSWORD_FAILURE_COUNT = 3;
    private static final List<Role> ROLES = List.of(Role.ACCESS);

    private static final String USER_IDENTIFIER = "Test@Email.Com";
    private static final String ORIGINAL_USERNAME = "old-username";
    private static final String ORIGINAL_EMAIL = "old@email.com";
    private static final long MARKED_FOR_DELETION_AT_EPOCH = MARKED_FOR_DELETION_AT.toEpochSecond(ZoneOffset.UTC);

    private static final User USER = User.builder()
        .userId(USER_ID)
        .email(EMAIL)
        .username(USERNAME)
        .password(PASSWORD)
        .language(LANGUAGE)
        .markedForDeletionAt(MARKED_FOR_DELETION_AT)
        .lockedUntil(LOCKED_UNTIL)
        .passwordFailureCount(PASSWORD_FAILURE_COUNT)
        .roles(ROLES)
        .build();

    @Mock
    private UserRepository userRepository;

    @Spy
    private final UuidConverter uuidConverter = new UuidConverter();

    @Spy
    private final DateTimeUtil dateTimeUtil = new DateTimeUtil();

    @InjectMocks
    private UserDao underTest;

    @Test
    void saveNew() {
        underTest.saveNew(USER);

        then(userRepository).should().trySaveCredential(USER_ID.toString(), USERNAME.toLowerCase());
        then(userRepository).should().trySaveCredential(USER_ID.toString(), EMAIL);
        then(userRepository).should().addRole(USER_ID.toString(), Role.ACCESS.name());

        ArgumentCaptor<ProfileEntity> captor = ArgumentCaptor.forClass(ProfileEntity.class);
        then(userRepository).should().save(captor.capture());
        ProfileEntity profile = captor.getValue();
        assertThat(profile.getUserId()).isEqualTo(USER_ID.toString());
        assertThat(profile.getEmail()).isEqualTo(EMAIL);
        assertThat(profile.getUsername()).isEqualTo(USERNAME);
        assertThat(profile.getPassword()).isEqualTo(PASSWORD);
        assertThat(profile.getLanguage()).isEqualTo(LANGUAGE);
        assertThat(profile.getPasswordFailureCount()).isEqualTo(PASSWORD_FAILURE_COUNT);
        assertThat(profile.getLockedUntil()).isEqualTo(LOCKED_UNTIL_EPOCH);
    }

    @Test
    void saveNew_usernameAlreadyExists() {
        ConditionalCheckFailedException exception = ConditionalCheckFailedException.builder().build();
        willThrow(exception).given(userRepository).trySaveCredential(USER_ID.toString(), USERNAME.toLowerCase());

        ExceptionValidator.validateNotLoggedException(() -> underTest.saveNew(USER), HttpStatus.CONFLICT, ErrorCode.USERNAME_ALREADY_EXISTS);

        then(userRepository).should(never()).save(any());
        then(userRepository).should(never()).addRole(any(), any());
    }

    @Test
    void saveNew_emailAlreadyExists() {
        ConditionalCheckFailedException exception = ConditionalCheckFailedException.builder().build();
        doNothing().when(userRepository).trySaveCredential(USER_ID.toString(), USERNAME.toLowerCase());
        willThrow(exception).given(userRepository).trySaveCredential(USER_ID.toString(), EMAIL);

        ExceptionValidator.validateNotLoggedException(() -> underTest.saveNew(USER), HttpStatus.CONFLICT, ErrorCode.EMAIL_ALREADY_EXISTS);

        then(userRepository).should(never()).save(any());
        then(userRepository).should(never()).addRole(any(), any());
        then(userRepository).should().deleteCredential(USERNAME.toLowerCase());
    }

    @Test
    void saveNew_equalUsernameAndEmail() {
        User user = User.builder()
            .userId(USER_ID)
            .email(EMAIL)
            .username(EMAIL)
            .password(PASSWORD)
            .language(LANGUAGE)
            .passwordFailureCount(PASSWORD_FAILURE_COUNT)
            .roles(ROLES)
            .build();

        underTest.saveNew(user);

        then(userRepository).should().trySaveCredential(USER_ID.toString(), EMAIL);
        then(userRepository).should().addRole(USER_ID.toString(), Role.ACCESS.name());

        ArgumentCaptor<ProfileEntity> captor = ArgumentCaptor.forClass(ProfileEntity.class);
        then(userRepository).should().save(captor.capture());
        ProfileEntity profile = captor.getValue();
        assertThat(profile.getUserId()).isEqualTo(USER_ID.toString());
        assertThat(profile.getEmail()).isEqualTo(EMAIL);
        assertThat(profile.getUsername()).isEqualTo(EMAIL);
        assertThat(profile.getPassword()).isEqualTo(PASSWORD);
        assertThat(profile.getLanguage()).isEqualTo(LANGUAGE);
        assertThat(profile.getPasswordFailureCount()).isEqualTo(PASSWORD_FAILURE_COUNT);
        assertThat(profile.getLockedUntil()).isEqualTo(0L);

        then(userRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void findByUserIdentifier() {
        ProfileEntity profile = ProfileEntity.builder()
            .userId(USER_ID.toString())
            .email(EMAIL)
            .username(USERNAME)
            .password(PASSWORD)
            .language(LANGUAGE)
            .passwordFailureCount(PASSWORD_FAILURE_COUNT)
            .lockedUntil(LOCKED_UNTIL_EPOCH)
            .build();
        TriWrapper<ProfileEntity, List<String>, Optional<Long>> wrapper = new TriWrapper<>(
            profile,
            List.of(Role.ACCESS.name()),
            Optional.of(MARKED_FOR_DELETION_AT_EPOCH)
        );
        given(userRepository.findByCredential(EMAIL)).willReturn(Optional.of(new CredentialEntity(EMAIL, USER_ID.toString())));
        given(userRepository.findByUserId(USER_ID.toString())).willReturn(Optional.of(wrapper));

        Optional<User> result = underTest.findByUserIdentifier(USER_IDENTIFIER);

        assertThat(result).isPresent();
        User user = result.get();
        assertThat(user.getUserId()).isEqualTo(USER_ID);
        assertThat(user.getEmail()).isEqualTo(EMAIL);
        assertThat(user.getUsername()).isEqualTo(USERNAME);
        assertThat(user.getPassword()).isEqualTo(PASSWORD);
        assertThat(user.getLanguage()).isEqualTo(LANGUAGE);
        assertThat(user.getPasswordFailureCount()).isEqualTo(PASSWORD_FAILURE_COUNT);
        assertThat(user.getLockedUntil()).isEqualTo(LOCKED_UNTIL.truncatedTo(ChronoUnit.SECONDS));
        assertThat(user.getMarkedForDeletionAt()).isEqualTo(MARKED_FOR_DELETION_AT.truncatedTo(ChronoUnit.SECONDS));
        assertThat(user.getRoles()).containsExactly(Role.ACCESS);
    }

    @Test
    void findByUserIdValidated_found() {
        ProfileEntity profile = ProfileEntity.builder()
            .userId(USER_ID.toString())
            .email(EMAIL)
            .username(USERNAME)
            .password(PASSWORD)
            .language(LANGUAGE)
            .passwordFailureCount(PASSWORD_FAILURE_COUNT)
            .lockedUntil(LOCKED_UNTIL_EPOCH)
            .build();
        TriWrapper<ProfileEntity, List<String>, Optional<Long>> wrapper = new TriWrapper<>(
            profile,
            List.of(Role.ACCESS.name()),
            Optional.of(MARKED_FOR_DELETION_AT_EPOCH)
        );
        given(userRepository.findByUserId(USER_ID.toString())).willReturn(Optional.of(wrapper));

        User result = underTest.findByUserIdValidated(USER_ID);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getPassword()).isEqualTo(PASSWORD);
        assertThat(result.getLanguage()).isEqualTo(LANGUAGE);
        assertThat(result.getPasswordFailureCount()).isEqualTo(PASSWORD_FAILURE_COUNT);
        assertThat(result.getLockedUntil()).isEqualTo(LOCKED_UNTIL.truncatedTo(ChronoUnit.SECONDS));
        assertThat(result.getMarkedForDeletionAt()).isEqualTo(MARKED_FOR_DELETION_AT.truncatedTo(ChronoUnit.SECONDS));
        assertThat(result.getRoles()).containsExactly(Role.ACCESS);
    }

    @Test
    void findByUserIdValidated_notFound() {
        given(userRepository.findByUserId(USER_ID.toString())).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.findByUserIdValidated(USER_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void addRole() {
        underTest.addRole(USER_ID, Role.TEST);

        then(userRepository).should().addRole(USER_ID.toString(), Role.TEST.name());
    }

    @Test
    void removeRole() {
        underTest.removeRole(USER_ID, Role.TEST);

        then(userRepository).should().deleteRole(USER_ID.toString(), Role.TEST.name());
    }

    @Test
    void saveProfile() {
        underTest.saveProfile(USER);

        ArgumentCaptor<ProfileEntity> captor = ArgumentCaptor.forClass(ProfileEntity.class);
        then(userRepository).should().save(captor.capture());
        ProfileEntity profile = captor.getValue();
        assertThat(profile.getUserId()).isEqualTo(USER_ID.toString());
        assertThat(profile.getEmail()).isEqualTo(EMAIL);
        assertThat(profile.getUsername()).isEqualTo(USERNAME);
        assertThat(profile.getPassword()).isEqualTo(PASSWORD);
        assertThat(profile.getLanguage()).isEqualTo(LANGUAGE);
        assertThat(profile.getPasswordFailureCount()).isEqualTo(PASSWORD_FAILURE_COUNT);
        assertThat(profile.getLockedUntil()).isEqualTo(LOCKED_UNTIL_EPOCH);
    }

    @Test
    void addRoleToAll() {
        given(userRepository.getAllUserIds()).willReturn(List.of(USER_ID.toString()));

        underTest.addRoleToAll(Role.ACCESS.name());

        then(userRepository).should().addRole(USER_ID.toString(), Role.ACCESS.name());
    }

    @Test
    void removeRoleFromAll() {
        given(userRepository.getAllUserIds()).willReturn(List.of(USER_ID.toString()));

        underTest.deleteRoleFromAll(Role.ACCESS.name());

        then(userRepository).should().deleteRole(USER_ID.toString(), Role.ACCESS.name());
    }

    @Test
    void updateMarkedForDeletion_markForDeletion() {
        underTest.updateMarkedForDeletion(USER);

        then(userRepository).should().markForDeletion(USER_ID.toString(), MARKED_FOR_DELETION_AT_EPOCH);
    }

    @Test
    void updateMarkedForDeletion_unmarkForDeletion() {
        User user = USER.toBuilder()
            .markedForDeletionAt(null)
            .build();

        underTest.updateMarkedForDeletion(user);

        then(userRepository).should().unmarkForDeletion(USER_ID.toString());
    }

    @Test
    void changeUsername() {
        underTest.changeUsername(ORIGINAL_USERNAME, USER);

        then(userRepository).should().trySaveCredential(USER_ID.toString(), USERNAME.toLowerCase());
        ArgumentCaptor<ProfileEntity> captor = ArgumentCaptor.forClass(ProfileEntity.class);
        then(userRepository).should().save(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo(USERNAME);
        then(userRepository).should().deleteCredential(ORIGINAL_USERNAME.toLowerCase());
    }

    @Test
    void changeUsername_alreadyExists() {
        ConditionalCheckFailedException exception = ConditionalCheckFailedException.builder().build();
        given(userRepository.findByCredential(USERNAME.toLowerCase())).willReturn(Optional.empty());
        willThrow(exception).given(userRepository).trySaveCredential(USER_ID.toString(), USERNAME.toLowerCase());

        Throwable ex = catchThrowable(() -> underTest.changeUsername(ORIGINAL_USERNAME, USER));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.USERNAME_ALREADY_EXISTS);
        then(userRepository).should(never()).save(any());
        then(userRepository).should(never()).deleteCredential(any());
    }

    @Test
    void changeUsername_existsForSameUser() {
        ConditionalCheckFailedException exception = ConditionalCheckFailedException.builder().build();
        given(userRepository.findByCredential(USERNAME.toLowerCase())).willReturn(Optional.of(new CredentialEntity(USERNAME.toLowerCase(), USER_ID.toString())));
        willThrow(exception).given(userRepository).trySaveCredential(USER_ID.toString(), USERNAME.toLowerCase());

        underTest.changeUsername(ORIGINAL_USERNAME, USER);

        then(userRepository).should().trySaveCredential(USER_ID.toString(), USERNAME.toLowerCase());
        ArgumentCaptor<ProfileEntity> captor = ArgumentCaptor.forClass(ProfileEntity.class);
        then(userRepository).should().save(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo(USERNAME);
        then(userRepository).should().deleteCredential(ORIGINAL_USERNAME.toLowerCase());
    }

    @Test
    void changeUsername_originalUsernameEqualsEmail() {
        underTest.changeUsername(EMAIL, USER);

        then(userRepository).should().trySaveCredential(USER_ID.toString(), USERNAME.toLowerCase());
        then(userRepository).should().save(any(ProfileEntity.class));
        then(userRepository).should(never()).deleteCredential(any());
    }

    @Test
    void changeEmail() {
        underTest.changeEmail(ORIGINAL_EMAIL, USER);

        then(userRepository).should().trySaveCredential(USER_ID.toString(), EMAIL);
        ArgumentCaptor<ProfileEntity> captor = ArgumentCaptor.forClass(ProfileEntity.class);
        then(userRepository).should().save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo(EMAIL);
        then(userRepository).should().deleteCredential(ORIGINAL_EMAIL);
    }

    @Test
    void changeEmail_alreadyExists() {
        ConditionalCheckFailedException exception = ConditionalCheckFailedException.builder().build();
        given(userRepository.findByCredential(EMAIL)).willReturn(Optional.empty());
        willThrow(exception).given(userRepository).trySaveCredential(USER_ID.toString(), EMAIL);

        Throwable ex = catchThrowable(() -> underTest.changeEmail(ORIGINAL_EMAIL, USER));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.EMAIL_ALREADY_EXISTS);
        then(userRepository).should(never()).save(any());
        then(userRepository).should(never()).deleteCredential(any());
    }

    @Test
    void changeEmail_existsForSameUser() {
        ConditionalCheckFailedException exception = ConditionalCheckFailedException.builder().build();
        given(userRepository.findByCredential(EMAIL)).willReturn(Optional.of(new CredentialEntity(EMAIL, USER_ID.toString())));
        willThrow(exception).given(userRepository).trySaveCredential(USER_ID.toString(), EMAIL);

        underTest.changeEmail(ORIGINAL_EMAIL, USER);

        then(userRepository).should().trySaveCredential(USER_ID.toString(), EMAIL);
        ArgumentCaptor<ProfileEntity> captor = ArgumentCaptor.forClass(ProfileEntity.class);
        then(userRepository).should().save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo(EMAIL);
        then(userRepository).should().deleteCredential(ORIGINAL_EMAIL);
    }

    @Test
    void changeEmail_originalEmailEqualsUsername() {
        User user = USER.toBuilder()
            .email(USERNAME)
            .build();

        underTest.changeEmail(USERNAME, user);

        then(userRepository).should().trySaveCredential(USER_ID.toString(), USERNAME);
        then(userRepository).should().save(any(ProfileEntity.class));
        then(userRepository).should(never()).deleteCredential(any());
    }

    @Test
    void getUsersMarkedForDeletion() {
        given(userRepository.getUserIdsMarkedForDeletion(any(Long.class))).willReturn(List.of(USER_ID.toString()));

        List<UUID> result = underTest.getUsersMarkedForDeletion();

        assertThat(result).containsExactly(USER_ID);
    }

    @Test
    void deleteByUserId() {
        ProfileEntity profile = ProfileEntity.builder()
            .userId(USER_ID.toString())
            .email(EMAIL)
            .username(USERNAME)
            .build();
        TriWrapper<ProfileEntity, List<String>, Optional<Long>> wrapper = new TriWrapper<>(
            profile,
            List.of(Role.ACCESS.name()),
            Optional.empty()
        );
        given(userRepository.findByUserId(USER_ID.toString())).willReturn(Optional.of(wrapper));

        underTest.deleteByUserId(USER_ID);

        then(userRepository).should().deleteCredential(USERNAME.toLowerCase());
        then(userRepository).should().deleteCredential(EMAIL);
        then(userRepository).should().deleteRole(USER_ID.toString(), Role.ACCESS.name());
        then(userRepository).should().unmarkForDeletion(USER_ID.toString());
        then(userRepository).should().deleteProfile(USER_ID.toString());
    }

    @Test
    void deleteByUserId_notFound() {
        given(userRepository.findByUserId(USER_ID.toString())).willReturn(Optional.empty());

        underTest.deleteByUserId(USER_ID);

        then(userRepository).should(never()).deleteCredential(any());
        then(userRepository).should(never()).deleteRole(any(), any());
        then(userRepository).should(never()).unmarkForDeletion(any());
        then(userRepository).should(never()).deleteProfile(any());
    }

    @Test
    void deleteByUserId_nullProfile() {
        TriWrapper<ProfileEntity, List<String>, Optional<Long>> wrapper = new TriWrapper<>(
            null,
            List.of(Role.ACCESS.name()),
            Optional.empty()
        );
        given(userRepository.findByUserId(USER_ID.toString())).willReturn(Optional.of(wrapper));

        underTest.deleteByUserId(USER_ID);

        then(userRepository).should(never()).deleteCredential(any());
        then(userRepository).should().deleteRole(USER_ID.toString(), Role.ACCESS.name());
        then(userRepository).should().unmarkForDeletion(USER_ID.toString());
        then(userRepository).should().deleteProfile(USER_ID.toString());
    }
}