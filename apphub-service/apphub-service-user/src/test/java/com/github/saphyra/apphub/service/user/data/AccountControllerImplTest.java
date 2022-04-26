package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.api.user.model.request.ChangeEmailRequest;
import com.github.saphyra.apphub.api.user.model.request.ChangePasswordRequest;
import com.github.saphyra.apphub.api.user.model.request.ChangeUsernameRequest;
import com.github.saphyra.apphub.api.user.model.request.RegistrationRequest;
import com.github.saphyra.apphub.api.user.model.response.AccountResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.service.account.ChangeEmailService;
import com.github.saphyra.apphub.service.user.data.service.account.ChangePasswordService;
import com.github.saphyra.apphub.service.user.data.service.account.ChangeUsernameService;
import com.github.saphyra.apphub.service.user.data.service.account.DeleteAccountService;
import com.github.saphyra.apphub.service.user.data.service.register.RegistrationService;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AccountControllerImplTest {
    private static final UUID USER_ID_1 = UUID.randomUUID();
    private static final String LOCALE = "locale";
    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";
    private static final String SEARCH_TEXT = "search-text";
    private static final String EMAIL = "email";
    private static final UUID USER_ID_2 = UUID.randomUUID();

    @Mock
    private ChangeEmailService changeEmailService;

    @Mock
    private ChangePasswordService changePasswordService;

    @Mock
    private ChangeUsernameService changeUsernameService;

    @Mock
    private DeleteAccountService deleteAccountService;

    @Mock
    private RegistrationService registrationService;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private AccountControllerImpl underTest;

    @Mock
    private RegistrationRequest registrationRequest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private ChangeEmailRequest changeEmailRequest;

    @Mock
    private ChangeUsernameRequest changeUsernameRequest;

    @Mock
    private ChangePasswordRequest changePasswordRequest;

    @Mock
    private User user;

    @Before
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID_1);
    }

    @Test
    public void changeEmail() {
        underTest.changeEmail(accessTokenHeader, changeEmailRequest);

        verify(changeEmailService).changeEmail(USER_ID_1, changeEmailRequest);
    }

    @Test
    public void changeUsername() {
        underTest.changeUsername(accessTokenHeader, changeUsernameRequest);

        verify(changeUsernameService).changeUsername(USER_ID_1, changeUsernameRequest);
    }

    @Test
    public void changePassword() {
        underTest.changePassword(accessTokenHeader, changePasswordRequest);

        verify(changePasswordService).changePassword(USER_ID_1, changePasswordRequest);
    }

    @Test
    public void deleteAccount() {
        underTest.deleteAccount(accessTokenHeader, new OneParamRequest<>(PASSWORD));

        verify(deleteAccountService).deleteAccount(USER_ID_1, PASSWORD);
    }

    @Test
    public void register() {
        underTest.register(registrationRequest, LOCALE);

        verify(registrationService).register(registrationRequest, LOCALE);
    }

    @Test
    public void getUsernameByUserId() {
        given(userDao.findByIdValidated(USER_ID_1)).willReturn(user);
        given(user.getUsername()).willReturn(USERNAME);

        String result = underTest.getUsernameByUserId(USER_ID_1);

        assertThat(result).isEqualTo(USERNAME);
    }

    @Test
    public void searchAccounts_tooShort() {
        Throwable ex = catchThrowable(() -> underTest.searchAccount(new OneParamRequest<>("as"), accessTokenHeader));

        ExceptionValidator.validateInvalidParam(ex, "value", "too short");
    }

    @Test
    public void searchAccounts_filterOwnAccount() {
        given(userDao.getByUsernameOrEmailContainingIgnoreCase(SEARCH_TEXT)).willReturn(List.of(user));
        given(user.getUserId()).willReturn(USER_ID_1);

        List<AccountResponse> result = underTest.searchAccount(new OneParamRequest<>(SEARCH_TEXT), accessTokenHeader);

        assertThat(result).isEmpty();
    }

    @Test
    public void searchAccounts() {
        given(userDao.getByUsernameOrEmailContainingIgnoreCase(SEARCH_TEXT)).willReturn(List.of(user));
        given(user.getUserId()).willReturn(USER_ID_2);
        given(user.getEmail()).willReturn(EMAIL);
        given(user.getUsername()).willReturn(USERNAME);

        List<AccountResponse> result = underTest.searchAccount(new OneParamRequest<>(SEARCH_TEXT), accessTokenHeader);

        assertThat(result).hasSize(1);
        AccountResponse response = result.get(0);
        assertThat(response.getUserId()).isEqualTo(USER_ID_2);
        assertThat(response.getEmail()).isEqualTo(EMAIL);
        assertThat(response.getUsername()).isEqualTo(USERNAME);
    }

    @Test
    public void getAccount() {
        given(userDao.findById(USER_ID_1)).willReturn(Optional.of(user));
        given(user.getUserId()).willReturn(USER_ID_1);
        given(user.getEmail()).willReturn(EMAIL);
        given(user.getUsername()).willReturn(USERNAME);

        AccountResponse result = underTest.getAccount(USER_ID_1);

        assertThat(result.getUserId()).isEqualTo(USER_ID_1);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
    }

    @Test
    public void getAccount_notFound() {
        given(userDao.findById(USER_ID_1)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.getAccount(USER_ID_1));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND);
    }

    @Test
    public void userExists() {
        given(userDao.findById(USER_ID_1)).willReturn(Optional.of(user));

        boolean result = underTest.userExists(USER_ID_1);

        assertThat(result).isTrue();
    }
}