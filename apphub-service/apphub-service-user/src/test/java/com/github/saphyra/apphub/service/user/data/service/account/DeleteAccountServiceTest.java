package com.github.saphyra.apphub.service.user.data.service.account;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.error_handler.exception.BadRequestException;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.encryption.impl.PasswordService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DeleteAccountServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PASSWORD_HASH = "password-hash";
    private static final String PASSWORD = "password";

    @Mock
    private EventGatewayApiClient eventGatewayApi;

    @Mock
    private PasswordService passwordService;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private DeleteAccountService underTest;

    @Mock
    private User user;

    @Captor
    private ArgumentCaptor<SendEventRequest<DeleteAccountEvent>> argumentCaptor;

    @Test
    public void nullPassword() {
        Throwable ex = catchThrowable(() -> underTest.deleteAccount(USER_ID, null));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams().get("password")).isEqualTo("must not be null");
    }

    @Test
    public void invalidPassword() {
        given(userDao.findById(USER_ID)).willReturn(user);
        given(user.getPassword()).willReturn(PASSWORD_HASH);
        given(passwordService.authenticate(PASSWORD, PASSWORD_HASH)).willReturn(false);

        Throwable ex = catchThrowable(() -> underTest.deleteAccount(USER_ID, PASSWORD));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.BAD_PASSWORD.name());
    }

    @Test
    public void deleteAccount() {
        given(userDao.findById(USER_ID)).willReturn(user);
        given(user.getPassword()).willReturn(PASSWORD_HASH);
        given(passwordService.authenticate(PASSWORD, PASSWORD_HASH)).willReturn(true);

        underTest.deleteAccount(USER_ID, PASSWORD);

        verify(eventGatewayApi).sendEvent(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getEventName()).isEqualTo(DeleteAccountEvent.EVENT_NAME);
        assertThat(argumentCaptor.getValue().getPayload().getUserId()).isEqualTo(USER_ID);
    }
}