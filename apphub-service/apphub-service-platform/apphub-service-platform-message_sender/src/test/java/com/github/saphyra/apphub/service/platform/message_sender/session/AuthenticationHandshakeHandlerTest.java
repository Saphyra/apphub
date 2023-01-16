package com.github.saphyra.apphub.service.platform.message_sender.session;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;

import java.security.Principal;
import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AuthenticationHandshakeHandlerTest {
    private static final String USER_ID_STRING = "user-id";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private WsSessionUserIdProvider userIdProvider;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private AuthenticationHandshakeHandler underTest;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private WebSocketHandler handler;

    @Test
    public void determinateUser() {
        given(userIdProvider.findUserId(request)).willReturn(USER_ID);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        Principal result = underTest.determineUser(request, handler, new HashMap<>());

        assertThat(result.getName()).isEqualTo(USER_ID_STRING);
    }
}