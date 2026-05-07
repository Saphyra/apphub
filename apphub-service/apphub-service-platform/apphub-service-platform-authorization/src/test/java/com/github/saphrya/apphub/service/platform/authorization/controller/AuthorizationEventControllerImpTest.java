package com.github.saphrya.apphub.service.platform.authorization.controller;

import com.github.saphrya.apphub.service.platform.authorization.service.LogoutService;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AuthorizationEventControllerImpTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private LogoutService logoutService;

    @InjectMocks
    private AuthorizationEventControllerImp underTest;

    @Test
    void deleteAccountEvent() {
        SendEventRequest<DeleteAccountEvent> request = SendEventRequest.<DeleteAccountEvent>builder()
            .payload(new DeleteAccountEvent(USER_ID))
            .build();

        underTest.deleteAccountEvent(request);

        then(logoutService).should().invalidateAllRefreshTokens(USER_ID);
    }
}