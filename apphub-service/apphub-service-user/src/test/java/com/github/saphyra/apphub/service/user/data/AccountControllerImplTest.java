package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.api.user.model.request.ChangeEmailRequest;
import com.github.saphyra.apphub.api.user.model.request.ChangePasswordRequest;
import com.github.saphyra.apphub.api.user.model.request.ChangeUsernameRequest;
import com.github.saphyra.apphub.api.user.model.request.RegistrationRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.service.account.ChangeEmailService;
import com.github.saphyra.apphub.service.user.data.service.account.ChangePasswordService;
import com.github.saphyra.apphub.service.user.data.service.account.ChangeUsernameService;
import com.github.saphyra.apphub.service.user.data.service.account.DeleteAccountService;
import com.github.saphyra.apphub.service.user.data.service.register.RegistrationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AccountControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LOCALE = "locale";
    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";

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
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void changeEmail() {
        underTest.changeEmail(accessTokenHeader, changeEmailRequest);

        verify(changeEmailService).changeEmail(USER_ID, changeEmailRequest);
    }

    @Test
    public void changeUsername() {
        underTest.changeUsername(accessTokenHeader, changeUsernameRequest);

        verify(changeUsernameService).changeUsername(USER_ID, changeUsernameRequest);
    }

    @Test
    public void changePassword() {
        underTest.changePassword(accessTokenHeader, changePasswordRequest);

        verify(changePasswordService).changePassword(USER_ID, changePasswordRequest);
    }

    @Test
    public void deleteAccount() {
        underTest.deleteAccount(accessTokenHeader, new OneParamRequest<>(PASSWORD));

        verify(deleteAccountService).deleteAccount(USER_ID, PASSWORD);
    }

    @Test
    public void register() {
        underTest.register(registrationRequest, LOCALE);

        verify(registrationService).register(registrationRequest, LOCALE);
    }

    @Test
    public void getUsernameByUserId() {
        given(userDao.findByIdValidated(USER_ID)).willReturn(user);
        given(user.getUsername()).willReturn(USERNAME);

        String result = underTest.getUsernameByUserId(USER_ID);

        assertThat(result).isEqualTo(USERNAME);
    }
}