package com.github.saphyra.apphub.lib.web_socket.core.handshake;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;

import java.security.Principal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthenticationHandshakeHandlerTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";

    @Mock
    private WsSessionUserIdProvider userIdProvider;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private AuthenticationHandshakeHandler underTest;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private WebSocketHandler webSocketHandler;

    @Test
    void determineUser() {
        given(userIdProvider.findUserId(request)).willReturn(Optional.of(USER_ID));
        given(uuidConverter.convertDomain(Optional.of(USER_ID))).willReturn(Optional.of(USER_ID_STRING));

        Principal result = underTest.determineUser(request, webSocketHandler, Collections.emptyMap());

        assertThat(result.getName()).isEqualTo(USER_ID_STRING);
    }
}