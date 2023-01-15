package com.github.saphyra.apphub.service.user.common;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.apphub.service.user.ban.dao.BanDao;
import com.github.saphyra.apphub.service.user.ban.service.RevokeBanService;
import com.github.saphyra.apphub.service.user.data.dao.role.RoleDao;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class UserEventControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LOCALE = "locale";
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();

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

    @Mock
    private BanDao banDao;

    @Mock
    private RevokeBanService revokeBanService;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private UserEventControllerImpl underTest;

    @Mock
    private User user;

    @Captor
    private ArgumentCaptor<SendEventRequest<DeleteAccountEvent>> argumentCaptor;

    @BeforeEach
    public void setUp() {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
    }

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
        verify(banDao).deleteByUserId(USER_ID);
    }

    @Test
    public void triggerAccountDeletion_futureMarkedForDeletionAt() {
        given(userDao.getUsersMarkedToDelete()).willReturn(Arrays.asList(user));
        given(user.getMarkedForDeletionAt()).willReturn(CURRENT_TIME.plusSeconds(1));

        underTest.triggerAccountDeletion();

        verifyNoInteractions(eventGatewayClient);
    }

    @Test
    public void triggerAccountDeletion_nullMarkedForDeletionAt() {
        given(userDao.getUsersMarkedToDelete()).willReturn(Arrays.asList(user));
        given(localeProvider.getLocaleValidated()).willReturn(LOCALE);
        given(user.getUserId()).willReturn(USER_ID);
        given(user.getMarkedForDeletionAt()).willReturn(null);

        underTest.triggerAccountDeletion();

        verify(eventGatewayClient).sendEvent(argumentCaptor.capture(), eq(LOCALE));

        SendEventRequest<DeleteAccountEvent> event = argumentCaptor.getValue();
        assertThat(event.getEventName()).isEqualTo(DeleteAccountEvent.EVENT_NAME);
        assertThat(event.getPayload().getUserId()).isEqualTo(USER_ID);
    }

    @Test
    public void triggerAccountDeletion_pastMarkedForDeletionAt() {
        given(userDao.getUsersMarkedToDelete()).willReturn(Arrays.asList(user));
        given(localeProvider.getLocaleValidated()).willReturn(LOCALE);
        given(user.getUserId()).willReturn(USER_ID);
        given(user.getMarkedForDeletionAt()).willReturn(CURRENT_TIME.minusSeconds(1));

        underTest.triggerAccountDeletion();

        verify(eventGatewayClient).sendEvent(argumentCaptor.capture(), eq(LOCALE));

        SendEventRequest<DeleteAccountEvent> event = argumentCaptor.getValue();
        assertThat(event.getEventName()).isEqualTo(DeleteAccountEvent.EVENT_NAME);
        assertThat(event.getPayload().getUserId()).isEqualTo(USER_ID);
    }

    @Test
    public void triggerRevokeExpiredBans() {
        underTest.triggerRevokeExpiredBans();

        verify(revokeBanService).revokeExpiredBans();
    }
}