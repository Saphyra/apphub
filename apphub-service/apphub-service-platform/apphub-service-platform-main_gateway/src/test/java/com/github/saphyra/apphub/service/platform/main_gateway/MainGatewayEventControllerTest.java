package com.github.saphyra.apphub.service.platform.main_gateway;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.service.platform.main_gateway.service.AccessTokenCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MainGatewayEventControllerTest {
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();

    @Mock
    private AccessTokenCache accessTokenCache;

    @InjectMocks
    private MainGatewayEventController underTest;

    @Test
    void accessTokenInvalidated() {
        SendEventRequest<List<UUID>> sendEventRequest = SendEventRequest.<List<UUID>>builder()
            .payload(List.of(ACCESS_TOKEN_ID))
            .build();

        underTest.accessTokenInvalidated(sendEventRequest);

        then(accessTokenCache).should().invalidate(ACCESS_TOKEN_ID);
    }
}