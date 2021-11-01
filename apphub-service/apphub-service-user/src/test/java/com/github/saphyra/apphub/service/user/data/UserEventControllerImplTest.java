package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.apphub.service.user.data.dao.role.RoleDao;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserEventControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LOCALE = "locale";

    @Mock
    private AccessTokenDao accessTokenDao;

    @Mock
    private RoleDao roleDao;

    @Mock
    private UserDao userDao;

    @Mock
    private EventGatewayApiClient eventGatewayClient;

    @Mock
    private LocaleProvider localeProvider;

    @InjectMocks
    private UserEventControllerImpl underTest;

    @Mock
    private User user;

    @Captor
    private ArgumentCaptor<SendEventRequest<DeleteAccountEvent>> argumentCaptor;

    @Test
    public void deleteAccountEvent() {
        DeleteAccountEvent event = new DeleteAccountEvent(USER_ID);
        SendEventRequest<DeleteAccountEvent> sendEventRequest = SendEventRequest.<DeleteAccountEvent>builder()
            .payload(event)
            .build();

        underTest.deleteAccountEvent(sendEventRequest);

        verify(accessTokenDao).deleteByUserId(USER_ID);
        verify(userDao).deleteById(USER_ID);
        verify(roleDao).deleteByUserId(USER_ID);
    }

    @Test
    public void triggerAccountDeletion() {
        given(userDao.getUsersMarkedToDelete()).willReturn(Arrays.asList(user));
        given(localeProvider.getLocaleValidated()).willReturn(LOCALE);
        given(user.getUserId()).willReturn(USER_ID);

        underTest.triggerAccountDeletion();

        verify(eventGatewayClient).sendEvent(argumentCaptor.capture(), eq(LOCALE));

        SendEventRequest<DeleteAccountEvent> event = argumentCaptor.getValue();
        assertThat(event.getEventName()).isEqualTo(DeleteAccountEvent.EVENT_NAME);
        assertThat(event.getPayload().getUserId()).isEqualTo(USER_ID);
    }
}