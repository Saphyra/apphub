package com.github.saphyra.apphub.service.user.controller;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.apphub.service.user.controller.UserEventControllerImpl;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserEventControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private AccessTokenDao accessTokenDao;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserEventControllerImpl underTest;

    @Test
    public void deleteAccountEvent() {
        DeleteAccountEvent event = new DeleteAccountEvent(USER_ID);
        SendEventRequest<DeleteAccountEvent> sendEventRequest = SendEventRequest.<DeleteAccountEvent>builder()
            .payload(event)
            .build();

        underTest.deleteAccountEvent(sendEventRequest);

        verify(accessTokenDao).deleteByUserId(USER_ID);
        verify(userDao).deleteById(USER_ID);
    }
}