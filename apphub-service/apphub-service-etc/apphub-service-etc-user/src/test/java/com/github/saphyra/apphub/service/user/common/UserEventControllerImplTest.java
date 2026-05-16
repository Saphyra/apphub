package com.github.saphyra.apphub.service.user.common;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.service.user.ban.service.RevokeBanService;
import com.github.saphyra.apphub.service.user.config.properties.UserProperties;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserEventControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();

    @Mock
    private UserDao userDao;

    @Mock
    private EventGatewayApiClient eventGatewayClient;

    @Mock
    private RevokeBanService revokeBanService;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private DeleteByUserIdDao deleteByUserIdDao;

    @Mock
    private UserProperties userProperties;

    private UserEventControllerImpl underTest;

    @Captor
    private ArgumentCaptor<SendEventRequest<DeleteAccountEvent>> argumentCaptor;

    @BeforeEach
    void setUp() {
        underTest = UserEventControllerImpl.builder()
            .userDao(userDao)
            .eventGatewayClient(eventGatewayClient)
            .revokeBanService(revokeBanService)
            .dateTimeUtil(dateTimeUtil)
            .deleteByUserIdDaos(List.of(deleteByUserIdDao))
            .userProperties(userProperties)
            .build();
    }

    @Test
    public void deleteAccountEvent() {
        DeleteAccountEvent event = new DeleteAccountEvent(USER_ID);
        SendEventRequest<DeleteAccountEvent> sendEventRequest = SendEventRequest.<DeleteAccountEvent>builder()
            .payload(event)
            .build();

        underTest.deleteAccountEvent(sendEventRequest);

        verify(deleteByUserIdDao).deleteByUserId(USER_ID);
    }

    @Test
    public void triggerAccountDeletion() {
        given(userDao.getUsersMarkedForDeletion()).willReturn(List.of(USER_ID));
        given(userProperties.getDeleteAccountBatchCount()).willReturn(1);

        underTest.triggerAccountDeletion();

        verify(eventGatewayClient).sendEvent(argumentCaptor.capture());

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